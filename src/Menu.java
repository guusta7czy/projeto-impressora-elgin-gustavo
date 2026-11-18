import java.util.Scanner;

public class Menu {
    private final Impressora impressora;
    private final Scanner entrada;

    public Menu(Impressora impressora, Scanner entrada) {
        this.impressora = impressora;
        this.entrada = entrada;
    }

    public void exibir() {
        int opcao;
        do {
            mostrarOpcoes();
            opcao = lerInteiro("Escolha uma opção: ");
            System.out.println();
            executar(opcao);
            System.out.println();
        } while (opcao != 0);
    }

    private void mostrarOpcoes() {
        String status = impressora.isConexaoAberta() ? "ABERTA" : "FECHADA";
        System.out.println("================ MENU ================");
        System.out.println("Conexão: " + status);
        System.out.println(" 1 - Configurar conexão");
        System.out.println(" 2 - Abrir conexão");
        System.out.println(" 3 - Fechar conexão");
        System.out.println(" 4 - Imprimir texto");
        System.out.println(" 5 - Imprimir QR Code");
        System.out.println(" 6 - Imprimir código de barras");
        System.out.println(" 7 - Imprimir XML SAT");
        System.out.println(" 8 - Imprimir XML de cancelamento SAT");
        System.out.println(" 9 - Abrir gaveta");
        System.out.println("10 - Sinal sonoro (bip)");
        System.out.println("11 - Avançar papel");
        System.out.println("12 - Ver status do papel");
        System.out.println(" 0 - Sair");
        System.out.println("======================================");
    }

    // Cada opção de impressão confere a conexão antes de pedir qualquer dado
    private void executar(int opcao) {
        switch (opcao) {
            case 1 -> configurarConexao();
            case 2 -> impressora.abrirConexao();
            case 3 -> impressora.fecharConexao();
            case 4 -> {
                if (!impressora.verificarConexao()) return;
                String texto = lerTexto("Digite o texto: ");
                int copias = lerInteiroEntre("Quantas cópias (1 a 10)? ", 1, 10);
                impressora.imprimirTexto(texto, copias);
            }
            case 5 -> {
                if (!impressora.verificarConexao()) return;
                impressora.imprimirQRCode(lerTexto("Conteúdo do QR Code (texto ou link): "));
            }
            case 6 -> {
                if (!impressora.verificarConexao()) return;
                impressora.imprimirCodigoBarras(lerTexto("Código (ex.: 012345678912): "));
            }
            case 7 -> {
                if (!impressora.verificarConexao()) return;
                String arquivo = lerTextoComPadrao("Arquivo XML [Enter = XMLSAT.xml]: ", "XMLSAT.xml");
                impressora.imprimirXMLSAT(arquivo);
            }
            case 8 -> {
                if (!impressora.verificarConexao()) return;
                String arquivo = lerTextoComPadrao("Arquivo XML [Enter = CANC_SAT.xml]: ", "CANC_SAT.xml");
                impressora.imprimirXMLCancelamentoSAT(arquivo);
            }
            case 9 -> impressora.abrirGaveta();
            case 10 -> {
                if (!impressora.verificarConexao()) return;
                impressora.sinalSonoro(lerInteiroEntre("Quantos bips (1 a 63)? ", 1, 63));
            }
            case 11 -> {
                if (!impressora.verificarConexao()) return;
                impressora.avancarPapel(lerInteiroEntre("Quantas linhas (1 a 20)? ", 1, 20));
            }
            case 12 -> impressora.verStatusPapel();
            case 0 -> {
                if (impressora.isConexaoAberta()) {
                    impressora.fecharConexao();
                }
                System.out.println("Saindo...");
            }
            default -> System.out.println("Opção inválida. Escolha um número do menu.");
        }
    }

    private void configurarConexao() {
        System.out.println("Configuração atual: " + impressora.getDescricaoConexao());
        System.out.println("Tipos: 1 = USB | 2 = Serial (RS232) | 3 = Rede (TCP/IP) | 4 = Bluetooth");

        int tipo = lerInteiroEntre("Tipo: ", 1, 4);
        String modelo = lerTextoComPadrao("Modelo [Enter = i9]: ", "i9");
        String conexao = lerTextoComPadrao("Conexão (USB, COM2, IP...) [Enter = USB]: ", "USB");
        int parametro = lerInteiro("Parâmetro (0 para USB, velocidade ou porta): ");

        impressora.configurar(tipo, modelo, conexao, parametro);
        if (impressora.isConexaoAberta()) {
            System.out.println("Feche e abra a conexão de novo para usar a nova configuração.");
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine();
    }

    private String lerTextoComPadrao(String mensagem, String padrao) {
        String texto = lerTexto(mensagem).trim();
        return texto.isEmpty() ? padrao : texto;
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(entrada.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite apenas números.");
            }
        }
    }

    private int lerInteiroEntre(String mensagem, int minimo, int maximo) {
        while (true) {
            int numero = lerInteiro(mensagem);
            if (numero >= minimo && numero <= maximo) {
                return numero;
            }
            System.out.println("Digite um número entre " + minimo + " e " + maximo + ".");
        }
    }
}
