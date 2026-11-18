import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Impressora {
    private static final String ASS_QRCODE_CANCELAMENTO =
            "Q5DLkpdRijIRGY6YSSNsTWK1TztHL1vD0V1Jc4spo/CEUqICEb9SFy82ym8EhBRZjbh3btsZhF+sjHqEMR159i4agru9x6KsepK/q0"
            + "E2e5xlU5cv3m1woYfgHyOkWDNcSdMsS6bBh2Bpq6s89yJ9Q6qh/J8YHi306ce9Tqb/drKvN2XdE5noRSS32TAWuaQEVd7u+TrvXlOQ"
            + "sE3fHR1D5f1saUwQLPSdIv01NF6Ny7jZwjCwv1uNDgGZONJdlTJ6p0ccqnZvuE70aHOI09elpjEO6Cd+orI7XHHrFCwhFhAcbalc+Z"
            + "fO5b/+vkyAHS6CYVFCDtYR9Hi5qgdk31v23w==";

    private int tipo = 1;
    private String modelo = "i9";
    private String conexao = "USB";
    private int parametro = 0;

    // Só vira true quando AbreConexaoImpressora devolver 0
    private boolean conexaoAberta = false;

    public void configurar(int tipo, String modelo, String conexao, int parametro) {
        this.tipo = tipo;
        this.modelo = modelo;
        this.conexao = conexao;
        this.parametro = parametro;
        System.out.println("Configuração salva: " + getDescricaoConexao());
    }

    public String getDescricaoConexao() {
        return "tipo=" + tipo + ", modelo=" + modelo + ", conexão=" + conexao + ", parâmetro=" + parametro;
    }

    public boolean isConexaoAberta() {
        return conexaoAberta;
    }

    public boolean verificarConexao() {
        if (!conexaoAberta) {
            System.out.println("Conexão fechada. Abra a conexão antes.");
            return false;
        }
        return true;
    }

    public void abrirConexao() {
        if (conexaoAberta) {
            System.out.println("A conexão já está aberta.");
            return;
        }
        try {
            int retorno = ImpressoraDLL.INSTANCE.AbreConexaoImpressora(tipo, modelo, conexao, parametro);
            if (retorno == 0) {
                conexaoAberta = true;
                System.out.println("Conexão aberta com sucesso! (" + getDescricaoConexao() + ")");
            } else {
                System.out.println("Erro ao abrir a conexão. Código: " + retorno);
                System.out.println("Confira se a impressora está ligada e o cabo USB conectado.");
            }
        } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
            // Acontece quando a DLL não é encontrada
            System.out.println("Não foi possível carregar a DLL: " + e.getMessage());
            System.out.println("Confira se o Working directory é a pasta principal do projeto"
                    + " (onde está a pasta \"Bibliotecas E1 Impressora\").");
        }
    }

    public void fecharConexao() {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.FechaConexaoImpressora();
        if (retorno == 0) {
            conexaoAberta = false;
            System.out.println("Conexão fechada com sucesso.");
        } else {
            System.out.println("Erro ao fechar a conexão. Código: " + retorno);
        }
    }

    public void imprimirTexto(String texto, int copias) {
        if (!verificarConexao()) return;
        for (int i = 1; i <= copias; i++) {
            int retorno = ImpressoraDLL.INSTANCE.ImpressaoTexto(texto + "\n", 1, 8, 0);
            // ImpressaoTexto devolve a quantidade de dados enviados, então só é erro se for negativo
            if (retorno < 0) {
                System.out.println("Erro ao imprimir a cópia " + i + ". Código: " + retorno);
                return;
            }
            System.out.println("Cópia " + i + " enviada.");
        }
        cortarPapel();
    }

    public void imprimirQRCode(String dados) {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoQRCode(dados, 6, 4);
        mostrarResultado("QR Code impresso!", "Erro ao imprimir o QR Code.", retorno);
        cortarPapel();
    }

    public void imprimirCodigoBarras(String dados) {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.ImpressaoCodigoBarras(8, "{A" + dados, 100, 2, 3);
        mostrarResultado("Código de barras impresso!", "Erro ao imprimir o código de barras.", retorno);
        cortarPapel();
    }

    public void imprimirXMLSAT(String caminhoArquivo) {
        if (!verificarConexao()) return;
        String xml = lerArquivo(caminhoArquivo);
        if (xml == null) return;

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLSAT(xml, 0);
        mostrarResultado("Cupom SAT impresso!", "Erro ao imprimir o XML SAT.", retorno);
        cortarPapel();
    }

    public void imprimirXMLCancelamentoSAT(String caminhoArquivo) {
        if (!verificarConexao()) return;
        String xml = lerArquivo(caminhoArquivo);
        if (xml == null) return;

        int retorno = ImpressoraDLL.INSTANCE.ImprimeXMLCancelamentoSAT(xml, ASS_QRCODE_CANCELAMENTO, 0);
        mostrarResultado("Cupom de cancelamento impresso!", "Erro ao imprimir o cancelamento SAT.", retorno);
        cortarPapel();
    }

    public void abrirGaveta() {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.AbreGaveta(1, 5, 10);
        mostrarResultado("Gaveta aberta!", "Erro ao abrir a gaveta.", retorno);
    }

    public void sinalSonoro(int quantidade) {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.SinalSonoro(quantidade, 50, 5);
        mostrarResultado("Bip!", "Erro no sinal sonoro.", retorno);
    }

    public void avancarPapel(int linhas) {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.AvancaPapel(linhas);
        mostrarResultado("Papel avançado " + linhas + " linha(s).", "Erro ao avançar o papel.", retorno);
    }

    public void verStatusPapel() {
        if (!verificarConexao()) return;
        int retorno = ImpressoraDLL.INSTANCE.StatusImpressora(3);
        switch (retorno) {
            case 5 -> System.out.println("Status: a impressora tem papel.");
            case 6 -> System.out.println("Status: o papel está acabando.");
            case 7 -> System.out.println("Status: sem papel!");
            default -> System.out.println("Não foi possível ler o status do papel. Código: " + retorno);
        }
    }

    private void mostrarResultado(String sucesso, String erro, int retorno) {
        if (retorno == 0) {
            System.out.println(sucesso);
        } else {
            System.out.println(erro + " Código: " + retorno);
        }
    }

    private void cortarPapel() {
        int retorno = ImpressoraDLL.INSTANCE.Corte(5);
        if (retorno != 0) {
            System.out.println("Aviso: não foi possível cortar o papel. Código: " + retorno);
        }
    }

    private String lerArquivo(String caminho) {
        Path arquivo = Paths.get(caminho);
        try {
            return Files.readString(arquivo);
        } catch (IOException e) {
            System.out.println("Arquivo não encontrado: " + arquivo.toAbsolutePath());
            return null;
        }
    }
}
