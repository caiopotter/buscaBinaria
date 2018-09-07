package servico;

import java.io.IOException;
import java.io.RandomAccessFile;

import buscaBinaria.Endereco;

public class BuscaBinaria {
	
	private int numeroDeIteracoes;
	private RandomAccessFile arquivo;
	private Endereco enderecoEncontrado;
	private String cepDigitado;
	private Long inicio, fim;
	private static final int BYTES_POR_REGISTRO_NO_ARQUIVO = 300;
	
	public BuscaBinaria(RandomAccessFile arquivo, String cepDigitado, Long inicio, Long fim) {
		this.setArquivo(arquivo);
		this.setCepDigitado(cepDigitado);
		this.setInicio(inicio);
		this.setFim(fim);
	}

	public void executarBuscaBinaria() throws IOException{
		Endereco endereco = new Endereco();
		this.numeroDeIteracoes++;
		arquivo.seek(((inicio+fim)/2)*BYTES_POR_REGISTRO_NO_ARQUIVO);
		endereco.leEndereco(arquivo);
		if(endereco.getCep().equals(cepDigitado)) {
			this.setEnderecoEncontrado(endereco);
		}else if(cepDigitado.compareTo(endereco.getCep()) > 0) {
			this.setInicio((arquivo.getFilePointer()+1)/BYTES_POR_REGISTRO_NO_ARQUIVO);
			executarBuscaBinaria();
		}else {
			this.setFim((arquivo.getFilePointer()-1)/BYTES_POR_REGISTRO_NO_ARQUIVO);
			executarBuscaBinaria();
		}
	}

	public int getNumeroDeIteracoes() {
		return numeroDeIteracoes;
	}
	
	public void setArquivo(RandomAccessFile arquivo) {
		this.arquivo = arquivo;
	}

	public Endereco getEnderecoEncontrado() {
		return enderecoEncontrado;
	}

	public void setEnderecoEncontrado(Endereco enderecoEncontrado) {
		this.enderecoEncontrado = enderecoEncontrado;
	}

	public void setCepDigitado(String cepDigitado) {
		this.cepDigitado = cepDigitado;
	}

	public void setInicio(Long inicio) {
		this.inicio = inicio;
	}

	public void setFim(Long fim) {
		this.fim = fim;
	}
	
}
