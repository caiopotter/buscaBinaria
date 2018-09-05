package buscaBinaria;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BuscaBinariaMain extends JFrame{
	
	private static final String INFO_AUTOR = "Criado por Caio Potter \n limapotter@gmail.com";
	private static final String ERRO_OCORRIDO = "Um erro ocorreu \n";
	private static final String NOME_ARQUIVO_DAT = "Arquivo dat";
	private static final String CEP_NÃO_ENCONTRADO = "CEP %s não encontrado.";
	private static final String CAMINHO_DE_ARQUIVO_VAZIO = "Caminho de arquivo vazio.";
	private static final int BYTES_POR_REGISTRO_NO_ARQUIVO = 300;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static int numeroDeIteracoes;
	static RandomAccessFile arquivo;
	static File caminhoJar;
	static JTextArea areadeTexto;
    static JFrame GUI;
	
	JTextField textoCaminhoArquivo;
	JButton botaoBuscaCEP, botaoBuscaCaminhoArquivo;
	JLabel labelCaminhoArquivo, labelCEP;
	JScrollPane scrollBar;
	JPanel gridPanel, flowPanel;
    JFormattedTextField textoCEP;
    JMenuBar menuBar;
    JMenu menuAjuda;
    JMenuItem acaoSobre;

	public static void main(String[] args) throws Exception{
		
		GUI = new BuscaBinariaMain();
        GUI.setVisible(true);
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public BuscaBinariaMain() throws ParseException, UnsupportedEncodingException, URISyntaxException {
		caminhoJar = recebeCaminhoJar();
		definePropriedadesJanela();
        instanciarComponentes();
        incluirListeners();
        adicionarComponentes();
	}

	private void definePropriedadesJanela() {
		gridPanel = new JPanel(new GridLayout(2, 3));
		flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		setSize(500,230);
        setTitle("Busca Binária");
	}

	private void incluirListeners() {
		botaoBuscaCaminhoArquivo.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  executaAcaoBuscaCaminhoArquivo();
			  }
		});
		
		botaoBuscaCEP.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  executaAcaoBuscaCEP();
			  }
		});
		acaoSobre.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  executaAcaoMenuSobre();
			  }
		});
	}

	private void instanciarComponentes() throws ParseException {
		labelCaminhoArquivo = new JLabel("Arquivo .dat:");
		labelCEP = new JLabel("Digite o CEP:");
        textoCaminhoArquivo = new JTextField();
        areadeTexto = new JTextArea(6, 40);
        areadeTexto.setEditable(false);
        scrollBar = new JScrollPane(areadeTexto);
        javax.swing.text.MaskFormatter formatacaoCEP = new javax.swing.text.MaskFormatter("#####-###");
        textoCEP = new javax.swing.JFormattedTextField(formatacaoCEP);
        botaoBuscaCEP = new JButton("Buscar CEP");
        botaoBuscaCaminhoArquivo = new JButton("Selecionar arquivo");
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuAjuda = new JMenu("Ajuda");
        acaoSobre = new JMenuItem("Sobre");
	}
	
	private void adicionarComponentes() {
		menuBar.add(menuAjuda);
		menuAjuda.add(acaoSobre);
		add(flowPanel);
        flowPanel.add(gridPanel);
        gridPanel.add(labelCaminhoArquivo);
        gridPanel.add(textoCaminhoArquivo);
        gridPanel.add(botaoBuscaCaminhoArquivo);
        gridPanel.add(labelCEP);
        gridPanel.add(textoCEP);
        gridPanel.add(botaoBuscaCEP);
        flowPanel.add(scrollBar);
	}

	private File recebeCaminhoJar() throws UnsupportedEncodingException, URISyntaxException {
		File caminhoJar = new File(BuscaBinariaMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		return caminhoJar;
	}

	private static RandomAccessFile selecionarArquivo(String caminhoArquivo) throws FileNotFoundException {
		return new RandomAccessFile(caminhoArquivo, "r");
	}

	private static void buscaBinaria(RandomAccessFile arquivo, String cepDigitado, Long inicio, Long fim) throws IOException {
		Endereco endereco = new Endereco();
		numeroDeIteracoes++;
		arquivo.seek(((inicio+fim)/2)*BYTES_POR_REGISTRO_NO_ARQUIVO);
		endereco.leEndereco(arquivo);
		if(endereco.getCep().equals(cepDigitado)) {
			escreverResultado(endereco);
		}else if(cepDigitado.compareTo(endereco.getCep()) > 0) {
			buscaBinaria(arquivo, cepDigitado, (arquivo.getFilePointer()+1)/BYTES_POR_REGISTRO_NO_ARQUIVO, fim);
		}else {
			buscaBinaria(arquivo, cepDigitado, inicio, (arquivo.getFilePointer()-1)/BYTES_POR_REGISTRO_NO_ARQUIVO);
		}
	}

	private static void escreverResultado(Endereco endereco) {
		areadeTexto.setText("CEP: " + endereco.getCep());
		areadeTexto.append("\n" + "Logradouro: " + endereco.getLogradouro());
		areadeTexto.append("\n" + "Bairro: " + endereco.getBairro());
		areadeTexto.append("\n" + "Cidade: " + endereco.getCidade());
		areadeTexto.append("\n" + "Estado: " + endereco.getEstado());
		areadeTexto.append("\n" + "Numero de iterações na busca binária: " + numeroDeIteracoes);
	}
	
	public void executaAcaoBuscaCEP() {
		String cep = textoCEP.getText().replace("-", "");
		try {
			arquivo = selecionarArquivo(textoCaminhoArquivo.getText());
			numeroDeIteracoes = 0;
			buscaBinaria(arquivo, cep, 0L, (arquivo.length()/BYTES_POR_REGISTRO_NO_ARQUIVO));
			arquivo.close();
		} catch (IOException e) {
			areadeTexto.setText(ERRO_OCORRIDO);
			if(textoCaminhoArquivo.getText().isEmpty()) {
				areadeTexto.append(CAMINHO_DE_ARQUIVO_VAZIO);
			}
			areadeTexto.append(e.getMessage());
		} catch (StackOverflowError e) {
			areadeTexto.setText(String.format(CEP_NÃO_ENCONTRADO, textoCEP.getText()));
		}
	}
	
	public void executaAcaoMenuSobre() {
		JOptionPane.showMessageDialog(GUI, INFO_AUTOR);
	}
	
	public void executaAcaoBuscaCaminhoArquivo() {
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(caminhoJar);
		fc.setFileFilter(new FileNameExtensionFilter(NOME_ARQUIVO_DAT, "dat"));
		int returnVal = fc.showOpenDialog(GUI);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			textoCaminhoArquivo.setText(fc.getSelectedFile().getPath());
		}
	}
}
