import java.util.Scanner;

public class Main {
    private static final int MAX_TENTATIVAS = 3;

    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);

        Usuario usuario = new Usuario("admin", "1234");
        Impressora impressora = new Impressora();

        System.out.println("==========================================");
        System.out.println("  SISTEMA DE CONTROLE - IMPRESSORA ELGIN");
        System.out.println("==========================================");

        if (fazerLogin(usuario, entrada)) {
            Menu menu = new Menu(impressora, entrada);
            menu.exibir();
        } else {
            System.out.println("Você errou " + MAX_TENTATIVAS + " vezes. Acesso bloqueado.");
        }

        entrada.close();
        System.out.println("Programa encerrado.");
    }

    private static boolean fazerLogin(Usuario usuario, Scanner entrada) {
        // Login com no máximo 3 tentativas
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            System.out.print("Usuário: ");
            String login = entrada.nextLine().trim();
            System.out.print("Senha: ");
            String senha = entrada.nextLine().trim();

            if (usuario.autenticar(login, senha)) {
                System.out.println("\nLogin realizado com sucesso! Bem-vindo, " + usuario.getLogin() + ".\n");
                return true;
            }

            int restantes = MAX_TENTATIVAS - tentativa;
            if (restantes > 0) {
                System.out.println("Usuário ou senha incorretos. Tentativas restantes: " + restantes + "\n");
            }
        }
        return false;
    }
}
