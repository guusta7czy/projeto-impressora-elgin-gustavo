public class Impressora {
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
}
