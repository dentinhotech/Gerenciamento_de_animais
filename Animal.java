// Classe base abstrata para representar os dados comuns a diferentes animais.
// Por ser abstrata, nao pode ser instanciada diretamente.
public abstract class Animal {
    // Identificador e dados compartilhados por qualquer tipo de animal.
    int id;
    String nome;
    String raca;
    int idade;

    // Inicializa os campos comuns. As subclasses chamam este construtor com super(...).
    public Animal(int id, String nome, String raca, int idade) {
        this.id = id;
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
    }

    // Cada subclasse deve fornecer sua propria forma de apresentar os detalhes.
    // A chamada feita usando uma referencia Animal sera resolvida pelo tipo real do objeto.
    public abstract String apresentarDetalhes();
}
