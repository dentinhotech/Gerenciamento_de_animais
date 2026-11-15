import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String URL =
            "jdbc:mariadb://localhost:3306/gerenciamento_animais?sslMode=disable";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    public static void main(String[] args) {
        try (Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
             Scanner scanner = new Scanner(System.in)) {
            int opcao = 0;

            while (opcao != 5) {
                System.out.println("\n=== CADASTRO DE CACHORROS ===");
                System.out.println("1. Cadastrar cachorro");
                System.out.println("2. Listar cachorros");
                System.out.println("3. Editar cachorro");
                System.out.println("4. Excluir cachorro");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opção: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                try {
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
                    System.err.println("Erro ao acessar o banco de dados: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Não foi possível conectar ao banco. Verifique se o MySQL/MariaDB "
                    + "do XAMPP está iniciado, se o banco existe e se usuário e senha estão corretos.");
            System.err.println("Detalhes: " + e.getMessage());
        }
    }

    private static void cadastrarCachorro(Connection conexao, Scanner scanner) throws SQLException {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Raça: ");
        String raca = scanner.nextLine();
        System.out.print("Idade: ");
        int idade = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO cachorros (nome, raca, idade) VALUES (?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, nome);
            comando.setString(2, raca);
            comando.setInt(3, idade);
            comando.executeUpdate();
        }
        System.out.println("Cachorro cadastrado com sucesso!");
    }

    private static void listarCachorros(Connection conexao) throws SQLException {
        ArrayList<Cachorro> cachorros = obterCachorros(conexao);
        System.out.println("\n--- LISTA DE CACHORROS ---");
        if (cachorros.isEmpty()) {
            System.out.println("Nenhum cachorro cadastrado.");
            return;
        }

        for (Cachorro cachorro : cachorros) {
            System.out.println("ID " + cachorro.id + " -> " + cachorro);
        }
    }

    private static void editarCachorro(Connection conexao, Scanner scanner) throws SQLException {
        listarCachorros(conexao);
        System.out.print("Digite o ID do cachorro para editar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Nova raça: ");
        String raca = scanner.nextLine();
        System.out.print("Nova idade: ");
        int idade = scanner.nextInt();
        scanner.nextLine();

        String sql = "UPDATE cachorros SET nome = ?, raca = ?, idade = ? WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, nome);
            comando.setString(2, raca);
            comando.setInt(3, idade);
            comando.setInt(4, id);
            if (comando.executeUpdate() == 0) {
                System.out.println("Não existe cachorro com esse ID.");
                return;
            }
        }
        System.out.println("Dados atualizados!");
    }

    private static void excluirCachorro(Connection conexao, Scanner scanner) throws SQLException {
        listarCachorros(conexao);
        System.out.print("Digite o ID do cachorro para excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM cachorros WHERE id = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            if (comando.executeUpdate() == 0) {
                System.out.println("Não existe cachorro com esse ID.");
                return;
            }
        }
        System.out.println("Cachorro removido!");
    }

    private static ArrayList<Cachorro> obterCachorros(Connection conexao) throws SQLException {
        ArrayList<Cachorro> cachorros = new ArrayList<>();
        String sql = "SELECT id, nome, raca, idade FROM cachorros ORDER BY id";
        try (PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultados = comando.executeQuery()) {
            while (resultados.next()) {
                cachorros.add(new Cachorro(
                        resultados.getInt("id"),
                        resultados.getString("nome"),
                        resultados.getString("raca"),
                        resultados.getInt("idade")));
            }
        }
        return cachorros;
    }
}
