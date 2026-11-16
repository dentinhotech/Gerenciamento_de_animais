"""Cadastro de cachorros no MariaDB/MySQL local do XAMPP.

O programa oferece operacoes de cadastro, consulta, edicao e exclusao (CRUD).
Dependencia: python -m pip install mysql-connector-python
"""

# O conector fornece a API Python para abrir conexoes e executar SQL.
import mysql.connector
# Error e a excecao base usada pelo conector para informar falhas de banco.
from mysql.connector import Error


# Configuracoes para o banco local. Ajuste usuario e senha se o XAMPP
# estiver configurado com credenciais diferentes.
CONFIGURACAO_BANCO = {
    "host": "localhost",
    "port": 3306,
    "database": "gerenciamento_animais",
    "user": "root",
    "password": "",
    "ssl_disabled": True,
}


def obter_cachorros(conexao):
    """Consulta todos os cachorros e devolve as linhas retornadas pelo banco."""
    # O cursor executa SQL; o bloco with garante que ele seja fechado ao sair.
    with conexao.cursor() as cursor:
        cursor.execute(
            "SELECT id, nome, raca, idade FROM cachorros ORDER BY id"
        )
        # fetchall retorna as linhas como tuplas, na ordem das colunas selecionadas.
        return cursor.fetchall()


def listar_cachorros(conexao):
    """Busca os registros e imprime cada cachorro, ou avisa se nao houver dados."""
    cachorros = obter_cachorros(conexao)
    print("\n--- LISTA DE CACHORROS ---")
    if not cachorros:
        print("Nenhum cachorro cadastrado.")
        return

    # Desempacota cada tupla retornada pelo SELECT para mostrar suas colunas.
    for identificador, nome, raca, idade in cachorros:
        print(
            f"ID {identificador} -> Nome: {nome} | "
            f"Raça: {raca} | Idade: {idade} anos"
        )


def ler_inteiro(mensagem):
    """Repete a pergunta ate que o usuario informe um numero inteiro valido."""
    while True:
        try:
            # input retorna texto; int converte esse texto para inteiro.
            return int(input(mensagem))
        except ValueError:
            # A entrada invalida nao encerra o programa: a pergunta sera repetida.
            print("Digite um número inteiro válido.")


def cadastrar_cachorro(conexao):
    """Le os dados do usuario e insere um novo cachorro na tabela."""
    nome = input("Nome: ")
    raca = input("Raça: ")
    idade = ler_inteiro("Idade: ")

    # %s sao marcadores do conector; os valores sao enviados separadamente.
    # Isso evita montar a consulta concatenando texto digitado pelo usuario.
    with conexao.cursor() as cursor:
        cursor.execute(
            "INSERT INTO cachorros (nome, raca, idade) VALUES (%s, %s, %s)",
            (nome, raca, idade),
        )
    # Confirma a transacao para que o INSERT fique persistido no banco.
    conexao.commit()
    print("Cachorro cadastrado com sucesso!")


def editar_cachorro(conexao):
    """Localiza um cachorro pelo ID e atualiza os dados informados."""
    # Exibe os registros para ajudar o usuario a escolher o ID correto.
    listar_cachorros(conexao)
    identificador = ler_inteiro("Digite o ID do cachorro para editar: ")

    # Verifica primeiro se o ID existe; sem registro, nao ha o que atualizar.
    with conexao.cursor() as cursor:
        cursor.execute(
            "SELECT id FROM cachorros WHERE id = %s",
            (identificador,),
        )
        if cursor.fetchone() is None:
            print("Não existe cachorro com esse ID.")
            return

        nome = input("Novo nome: ")
        raca = input("Nova raça: ")
        idade = ler_inteiro("Nova idade: ")
        # Atualiza apenas a linha cujo ID corresponde ao informado.
        cursor.execute(
            "UPDATE cachorros SET nome = %s, raca = %s, idade = %s WHERE id = %s",
            (nome, raca, idade, identificador),
        )

    # Persiste a alteracao executada pelo UPDATE.
    conexao.commit()
    print("Dados atualizados!, cachorro ID:", identificador)


def excluir_cachorro(conexao):
    """Remove da tabela o cachorro que possui o ID informado."""
    listar_cachorros(conexao)
    identificador = ler_inteiro("Digite o ID do cachorro para excluir: ")

    # Executa DELETE parametrizado e guarda a quantidade de linhas removidas.
    with conexao.cursor() as cursor:
        cursor.execute(
            "DELETE FROM cachorros WHERE id = %s",
            (identificador,),
        )
        removidos = cursor.rowcount

    # rowcount igual a zero significa que nenhum registro tinha esse ID.
    if removidos == 0:
        print("Não existe cachorro com esse ID.")
        return

    # Confirma a exclusao para torna-la permanente.
    conexao.commit()
    print("Cachorro removido!")


def main():
    """Abre a conexao, executa o menu e garante o fechamento da conexao."""
    try:
        # A conexao e criada uma vez e compartilhada pelas operacoes do menu.
        conexao = mysql.connector.connect(**CONFIGURACAO_BANCO)
    except Error as erro:
        # Trata falhas de conexao, como servidor parado ou credenciais incorretas.
        print(
            "Não foi possível conectar ao banco. Verifique se o MySQL/MariaDB "
            "do XAMPP está iniciado, se o banco existe e se usuário e senha "
            "estão corretos."
        )
        print(f"Detalhes: {erro}")
        return

    try:
        # Mantem o programa ativo ate o usuario selecionar a opcao de sair.
        while True:
            print("\n=== CADASTRO DE CACHORROS ===")
            print("1. Cadastrar cachorro")
            print("2. Listar cachorros")
            print("3. Editar cachorro")
            print("4. Excluir cachorro")
            print("5. Sair")
            opcao = input("Escolha uma opção: ").strip()

            try:
                # Cada opcao chama a funcao responsavel por uma operacao do sistema.
                if opcao == "1":
                    cadastrar_cachorro(conexao)
                elif opcao == "2":
                    listar_cachorros(conexao)
                elif opcao == "3":
                    editar_cachorro(conexao)
                elif opcao == "4":
                    excluir_cachorro(conexao)
                elif opcao == "5":
                    print("Programa encerrado.")
                    break
                else:
                    print("Opção inválida.")
            except Error as erro:
                # Desfaz uma transacao incompleta e mostra o erro do banco.
                conexao.rollback()
                print(f"Erro ao acessar o banco de dados: {erro}")
    finally:
        # Fecha a conexao mesmo se ocorrer erro durante o uso do menu.
        conexao.close()


# Executa main somente quando este arquivo for iniciado diretamente.
# Se for importado por outro modulo, o menu nao sera iniciado automaticamente.
if __name__ == "__main__":
    main()
