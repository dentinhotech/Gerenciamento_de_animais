public class Cachorro {
    int id;
    String nome;
    String raca;
    int idade;

    public Cachorro(String nome, String raca, int idade) {
        this(0, nome, raca, idade);
    }

    public Cachorro(int id, String nome, String raca, int idade) {
        this.id = id;
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
    }

    // Sobrescrevendo o método padrão do Java para facilitar a impressão
    @Override
    public String toString() {
        return "Nome: " + this.nome + " | Raça: " + this.raca + " | Idade: " + this.idade + " anos";
    }
}
