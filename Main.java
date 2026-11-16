// Classes JDBC usadas para conectar ao banco e executar comandos SQL.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// ArrayList guarda temporariamente os animais consultados; Scanner le as entradas do terminal.
import java.util.ArrayList;
import java.util.Scanner;

// Classe principal: apresenta o menu e coordena as operacoes do sistema.
public class Main {
    // Endereco do MariaDB local do XAMPP. sslMode=disable evita exigir SSL no servidor local.
    private static final String URL =
            "jdbc:mariadb://localhost:3306/gerenciamento_animais?sslMode=disable";

    // Credenciais do banco. Altere SENHA se o usuario root tiver uma senha configurada.
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    // Ponto de entrada da aplicacao.
    public static void main(String[] args) {
        // try-with-resources fecha automaticamente a conexao e o Scanner ao terminar o bloco.
        try (Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
             Scanner scanner = new Scanner(System.in)) {
            int opcao = 0;

            // Mantem o menu ativo ate que o usuario escolha a opcao 5.
            while (opcao != 5) {
                System.out.println("\n=== CADASTRO DE CACHORROS ===");
                System.out.println("1. Cadastrar cachorro");
                System.out.println("2. Listar cachorros");
                System.out.println("3. Editar cachorro");
                System.out.println("4. Excluir cachorro");
                System.out.println("5. Sair");
                opcao = lerInteiro(scanner, "Escolha uma opção: ");

                try {
                    // Encaminha a escolha para a operacao correspondente.
                    switch (opcao) {
                        case 1:
                            cadastrarCachorro(conexao, scanner);
                            break;
                        case 2:
                            listarCachorros(conexao);
                            break;
                        case 3:
                            editarCachorro(conexao, scanner);
                            break;
                        case 4:
                            excluirCachorro(conexao, scanner);
                            break;
                        case 5:
                            System.out.println("Programa encerrado.");
                            break;
                        default:
                            System.out.println("Opção inválida.");
                    }
                } catch (SQLException e) {
                    // Informa erros ocorridos durante uma consulta ou alteracao no banco.
                    System.err.println("Erro ao acessar o banco de dados: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            // Este bloco trata falhas ao abrir a conexao, antes de o menu ser iniciado.
            System.err.println("Não foi possível conectar ao banco. Verifique se o MySQL/MariaDB "
                    + "do XAMPP está iniciado, se o banco existe e se usuário e senha estão corretos.");
            System.err.println("Detalhes: " + e.getMessage());
        }
    }

    // Le os dados do cachorro e grava um novo registro na tabela.
    private static void cadastrarCachorro(Connection conexao, Scanner scanner) throws SQLException {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Raça: ");
        String raca = scanner.nextLine();
        int idade = lerInteiro(scanner, "Idade: ");

        // Os ? sao parametros preenchidos abaixo, evitando concatenar os dados na instrucao SQL.
        String sql = "INSERT INTO cachorros (nome, raca, idade) VALUES (?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            // Os indices dos parametros comecam em 1 e seguem a ordem dos ? na consulta.
            comando.setString(1, nome);
            comando.setString(2, raca);
            comando.setInt(3, idade);
            // Executa o INSERT; o banco gera automaticamente o ID do novo registro.
            comando.executeUpdate();
        }
        System.out.println("Cachorro cadastrado com sucesso!, Seu Cachorro foi " + nome + " Da raça " + raca + " E tem " + idade + " anos de idade.");
    }

    // Consulta os registros e apresenta cada cachorro no terminal.
    private static void listarCachorros(Connection conexao) throws SQLException {
        ArrayList<Animal> cachorros = obterCachorros(conexao);
        System.out.println("\n--- LISTA DE CACHORROS ---");
        if (cachorros.isEmpty()) {
            System.out.println("Nenhum cachorro cadastrado.");
            return;
        }

        // Polimorfismo: a lista usa o tipo Animal, mas cada objeto e Cachorro.
        // A chamada executa a versao de apresentarDetalhes sobrescrita em Cachorro.
        for (Animal animal : cachorros) {
            System.out.println("ID " + animal.id + " -> " + animal.apresentarDetalhes());
        }
    }

    // Atualiza nome, raca e idade do registro selecionado pelo ID.
    private static void editarCachorro(Connection conexao, Scanner scanner) throws SQLException {
        // Mostra os IDs disponiveis antes de solicitar qual registro sera alterado.
        listarCachorros(conexao);
        int id = lerInteiro(scanner, "Digite o ID do cachorro para editar: ");

        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Nova raça: ");
        String raca = scanner.nextLine();
        int idade = lerInteiro(scanner, "Nova idade: ");

        // O WHERE limita a alteracao ao cachorro cujo ID foi informado.
        String sql = "UPDATE cachorros SET nome = ?, raca = ?, idade = ? WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, nome);
            comando.setString(2, raca);
            comando.setInt(3, idade);
            comando.setInt(4, id);
            // executeUpdate retorna quantas linhas foram alteradas; zero indica ID inexistente.
            if (comando.executeUpdate() == 0) {
                System.out.println("Não existe cachorro com esse ID.");
                return;
            }
        }
        System.out.println("Dados atualizados!");
    }

    // Remove do banco o registro identificado pelo ID digitado.
    private static void excluirCachorro(Connection conexao, Scanner scanner) throws SQLException {
        listarCachorros(conexao);
        int id = lerInteiro(scanner, "Digite o ID do cachorro para excluir: ");

        // O parametro evita inserir diretamente a entrada do usuario no SQL.
        String sql = "DELETE FROM cachorros WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            // Se nenhuma linha for removida, o ID nao existe na tabela.
            if (comando.executeUpdate() == 0) {
                System.out.println("Não existe cachorro com esse ID.");
                return;
            }
        }
        System.out.println("Cachorro removido!" + " O cachorro com ID " + id +  " foi excluído do sistema.");
    }

    // Le todos os cachorros do banco e converte cada linha em um objeto Cachorro.
    // O retorno e tipado como Animal para permitir o uso de polimorfismo na listagem.
    private static ArrayList<Animal> obterCachorros(Connection conexao) throws SQLException {
        ArrayList<Animal> cachorros = new ArrayList<>();
        String sql = "SELECT id, nome, raca, idade FROM cachorros ORDER BY id";
        try (PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultados = comando.executeQuery()) {
            // Cada chamada a next() avanca para a proxima linha do resultado.
            while (resultados.next()) {
                // Cria um Cachorro com os dados da linha atual e o adiciona a lista de Animal.
                cachorros.add(new Cachorro(
                        resultados.getInt("id"),
                        resultados.getString("nome"),
                        resultados.getString("raca"),
                        resultados.getInt("idade")));
            }
        }
        return cachorros;
    }

    // Le uma linha e tenta converte-la para inteiro. Se o valor nao for numerico,
    // informa o usuario e repete a leitura em vez de encerrar o programa.
    private static int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine();
            try {
                // trim remove espacos nas extremidades antes da conversao.
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }
}
