import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

public class Main{

static boolean resolvido; // indica se uma solucao ja foi encontrada
static int totVal,totTrans; // variaveis para indicar o numero de valores e a soma real deles  
static int [] valores = new int [40]; // arranjo para guardar os valores 
// arranjo para guardar a solucao
static char [] result = {'*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*',
						 '*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*','*'};	
	
	// metodo usado pelas threads para alterar a solucao
	static synchronized boolean altera(char [] resultAux){
		// if que finaliza a busca por solucoes caso passe o tempo limite	
		if(resultAux ==  null) return resolvido = true;
		// if que retona true se o problema ja ter sido resolvido
		if(resolvido) return true;

	// for que checa se a solucao eh valida
	int achou = 0;	
		for(int i = 0;i < totVal;i++){
			if(result [i] != '?'){
				if(result [i] != resultAux [i]){
					if(result [i] == '*') result [i] = resultAux [i];
					else{
					result [i] = '?';
					achou++;	
					}
				}
			}
			else achou++;
		}
		// if que checa se os sinais de todos os valores foram encontrados,
		// retornando true se verdadeiro 
		if(achou == totVal) return resolvido = true;

	// retorna false uma vez que a solucao eh inviavel
	return false;	
	}
	
	// metodo recursivo que busca por uma solucao
	static boolean trata(int at,int soma,char [] resultAux){
		// retorna true se o problema ja foi resolvido
		if(resolvido) return true;
		// se chegou no ultimo valor, checa se achou um solucao valida
		// ou false caso nao seja
		if(at == totVal)
		{
			if(soma == totTrans && altera(resultAux)) return true;
			else return false;
		}
	
	// coloca valor negativo no valor atual e faz uma chamada recursiva
	resultAux [at] = '-';	
		if(trata(at + 1,soma - valores [at],resultAux) == false)
		{
			// coloca valor positivo no valor atual e faz uma chamada recursiva
			resultAux [at] = '+';
			return trata(at + 1,soma + valores [at],resultAux);
		}
	
	// retorna true uma vez que achou uma solucao
	return true;
	}
	
	// metodo que tenta encontrar uma solucao de uma forma randomica,
	// mas sempre negando os sinais dos valores cujo sinal ja foi determinado 
	static void trataNeg(char [] resultAux,Random random)
	{
		// while que busca por solucoes enquanto uma nao ser encontrada
		int i,soma;

		while(resolvido == false)
		{
			// for que determina um sinal para cada valor de uma forma randomica
			// ou negando o que ja foi determinado para o valor
			for(i = soma = 0;i < totVal;i++)
			{
				if((result [i] == '+' || result [i] == '-') && random.nextDouble() < 0.7)
				{
					if(result [i] == '+') 
					{
						resultAux [i] = '-';
						soma -= valores [i];
					}
					else 
					{
						resultAux [i] = '+';
						soma += valores [i];
					}
				}
				else
				{
					if(random.nextDouble() < 0.5 - ((float) (totTrans - soma)%51)/100)
					{
						resultAux [i] = '+';
						soma += valores [i];
					}
					else 
					{
						resultAux [i] = '-';
						soma -= valores [i];
					}
				}
			}	
			// checa se a solucao encontrada eh valida
			if(soma == totTrans && altera(resultAux)) return;
		}
	}
	
	// metodo que tenta encontrar um solucao comecando com todos os sinais negativos e
	// setando cada eles para negativo randomicamente enquanto a soma nao ser menor que o totTrans	
	static void trataAnt(char [] resultAux,LinkedList <Integer> possiveis,Random random)
	{
		// while que busca por solucoes enquanto uma nao ser encontrada
		int soma,aux;

		possiveis.clear();
		while(resolvido == false)
		{
			// for que reinicia o arranjo de sinais positivos que podem virar negativos
			for(aux = soma = 0;aux < totVal;aux++)
			{
				resultAux [aux] = '+';
				soma += valores [aux];

				if(possiveis.contains(aux) == false) possiveis.add(aux);
			}
			// do while que muda os sinais de positivos para negativo randomicamente 
			// enquanto a soma nao for menor que totTrans e houver sinais para trocar 
			do
			{
				aux = random.nextInt(possiveis.size());
				aux = possiveis.remove(aux);
				resultAux [aux] = '-';
				soma -= valores [aux]*2;
			} while(soma > totTrans && possiveis.size() > 1);
			// if que checa se a solucao eh valida
			if(soma == totTrans && altera(resultAux)) return;
		}
	}
	public static void main(String [] args)
	{
		// faz a leitura do arquivo e armazena as entradas
		LinkedList <String> inputs;
			try(BufferedReader reader = new BufferedReader(new FileReader("instancias/entrada_3.txt"))) 
			{
				String input = null;
				inputs = new LinkedList<String>();
				
				while ((input = reader.readLine()) != null) 
				{
					inputs.addLast(input);
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return;
			}
		
		// arranjos usados pelas threads
		Thread [] thrs = new Thread [4];
		char [] resultAux = new char [40],
				resultAuxNeg = new char [40],
				resultAuxAnt = new char [40];

		// lista usada pela thread ant e Random usados pelas threads
		LinkedList <Integer> list = new LinkedList <Integer> ();		
		Random random = new Random();

		// while que executa enquanto a numero de valores não for 0
		String [] arr = inputs.removeFirst().split(" ");
		totVal = Integer.parseInt(arr[0]);
		while(totVal > 0)
		{
			totTrans = Integer.parseInt(arr[1]);
			
				// pega os valores e armazena no arranjo 
				for(int i = 0;i < totVal;i++) 
					valores [i] = Integer.parseInt(inputs.removeFirst());
				
				// inicia as threads que resolvem o problema 
				thrs [0] = new Thread(new Runnable()
				{
					@Override
					public void run(){	
						if(!(trata(0,0,resultAux))) resolvido = true;
					}
				});
				thrs [1] = new Thread(new Runnable()
				{
					@Override
					public void run(){	
						trataNeg(resultAuxNeg,random);
					}
				});		
				thrs [2] = new Thread(new Runnable()
				{
					@Override
					public void run(){	
						trataAnt(resultAuxAnt,list,random);
					}
				});		
				thrs [3] = new Thread(new Runnable()
				{
					@Override
					public void run(){	
						long cont = System.currentTimeMillis();
						while(resolvido == false){
							if(System.currentTimeMillis() - cont > 80){
								altera(null);
							}
						}
					}
				});
				
			// comeca a resolver o problema
			resolvido = false;
				try{
					for(int i = 0;i < thrs.length;i++) thrs [i].start();
					for(int i = 0;i < thrs.length;i++) thrs [i].join();
				}
				catch(Throwable e){
				e.printStackTrace();
				}
			
			// printa a resposta
			totTrans = 0;
				do
				{
					System.out.print(result [totTrans]);
					result [totTrans++] = '*';
				} while(totTrans < 40 && result [totTrans] != '*');
			System.out.println();

			// pega a novo numero de valores
			arr = inputs.removeFirst().split(" ");
			totVal = Integer.parseInt(arr[0]);
		}
	}
}