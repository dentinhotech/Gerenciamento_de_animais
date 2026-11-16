// Cachorro herda de Animal os campos id, nome, raca e idade.
public class Cachorro extends Animal {

    // Construtor conveniente para criar um cachorro antes de receber um ID do banco.
    // O valor zero e apenas inicial; no cadastro, o banco gera o identificador.
    public Cachorro(String nome, String raca, int idade) {
        this(0, nome, raca, idade);
    }

    // Construtor usado ao reconstruir um cachorro com os dados lidos do banco.
    public Cachorro(int id, String nome, String raca, int idade) {
        // Inicializa os campos herdados pelo construtor da classe Animal.
        super(id, nome, raca, idade);
    }

    // Implementa o metodo abstrato de Animal com a apresentacao especifica do cachorro.
    // @Override permite ao compilador verificar que o metodo sobrescrito existe na classe base.
    @Override
    public String apresentarDetalhes() {
        return "Nome: " + this.nome + " | Raça: " + this.raca + " | Idade: " + this.idade + " anos";
    }

    // Define a representacao textual do cachorro; reutiliza a apresentacao acima.
    @Override
    public String toString() {
        return apresentarDetalhes();
    }
}
