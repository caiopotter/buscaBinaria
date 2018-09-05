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

public class BuscaBinariaMain extends JFrame implements ActionListener{
	
	private static final String ERRO_OCORRIDO = "Um erro ocorreu \n";
	private static final String NOME_ARQUIVO_DAT = "Arquivo dat";
	private static final String CEP_NÃO_ENCONTRADO = "CEP %s não encontrado.";
	private static final String CAMINHO_DE_ARQUIVO_VAZIO = "Caminho de arquivo vazio.";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int numeroDeIteracoes = 0;
	static RandomAccessFile arquivo;
	static File caminhoJar;
	
	static JTextArea areadeTexto;
	static JTextField textoCaminhoArquivo;
	static JButton botaoBuscaCEP, botaoBuscaCaminhoArquivo;
	static JLabel labelCaminhoArquivo, labelCEP;
	static JScrollPane scrollBar;
    static JPanel gridPanel, flowPanel;
    static JFormattedTextField textoCEP;
    static JMenuBar menuBar;
    static JMenu menuAjuda;
    static JMenuItem acaoSobre;
    static JFrame GUI;

	public static void main(String[] args) throws Exception{
		
		GUI = new BuscaBinariaMain();
        GUI.setVisible(true);
        GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public BuscaBinariaMain() throws ParseException, UnsupportedEncodingException, URISyntaxException {
		caminhoJar = recebeCaminhoJar();
		gridPanel = new JPanel(new GridLayout(2, 3));
		flowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		setSize(570,360);
        setTitle("Busca Binária");
        
        labelCaminhoArquivo = new JLabel("Arquivo .dat:");
        textoCaminhoArquivo = new JTextField();
        botaoBuscaCaminhoArquivo = new JButton("Selecionar arquivo");
        labelCEP = new JLabel("Digite o CEP:");
        javax.swing.text.MaskFormatter formatacaoCEP = new javax.swing.text.MaskFormatter("#####-###");
        textoCEP = new javax.swing.JFormattedTextField(formatacaoCEP);
        botaoBuscaCEP = new JButton("Buscar CEP");
        botaoBuscaCaminhoArquivo.addActionListener(this);
        botaoBuscaCEP.addActionListener(this);
        areadeTexto = new JTextArea(14, 40);
        areadeTexto.setEditable(false);
        scrollBar = new JScrollPane(areadeTexto);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuAjuda = new JMenu("Ajuda");
        menuBar.add(menuAjuda);
        acaoSobre = new JMenuItem("Sobre");
        menuAjuda.add(acaoSobre);
        acaoSobre.addActionListener(this);
        
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
		arquivo.seek(((inicio+fim)/2)*300);
		endereco.leEndereco(arquivo);
		if(endereco.getCep().equals(cepDigitado)) {
			areadeTexto.setText("CEP: " + endereco.getCep());
			areadeTexto.append("\n" + "Logradouro: " + endereco.getLogradouro());
			areadeTexto.append("\n" + "Bairro: " + endereco.getBairro());
			areadeTexto.append("\n" + "Cidade: " + endereco.getCidade());
			areadeTexto.append("\n" + "Estado: " + endereco.getEstado());
			areadeTexto.append("\n" + "Numero de iterações na busca binária: " + numeroDeIteracoes);
		}else if(cepDigitado.compareTo(endereco.getCep()) > 0) {
			buscaBinaria(arquivo, cepDigitado, (arquivo.getFilePointer()+1)/300, fim);
		}else {
			buscaBinaria(arquivo, cepDigitado, inicio, (arquivo.getFilePointer()-1)/300);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evento) {
		if(evento.getSource() == acaoSobre) {
			JOptionPane.showMessageDialog(GUI, "Criado por Caio Potter \n limapotter@gmail.com");
		}
		if(evento.getSource() == botaoBuscaCEP) {
			String cep = textoCEP.getText().replace("-", "");
			try {
				arquivo = selecionarArquivo(textoCaminhoArquivo.getText());
				numeroDeIteracoes = 0;
				buscaBinaria(arquivo, cep, 0L, (arquivo.length()/300));
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
		if(evento.getSource() == botaoBuscaCaminhoArquivo) {
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(caminhoJar);
			fc.setFileFilter(new FileNameExtensionFilter(NOME_ARQUIVO_DAT, "dat"));
			int returnVal = fc.showOpenDialog(GUI);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				textoCaminhoArquivo.setText(fc.getSelectedFile().getPath());
			}
		}
		
	}

}
