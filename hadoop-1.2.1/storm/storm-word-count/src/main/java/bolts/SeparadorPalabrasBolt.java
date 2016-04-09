package bolts;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SeparadorPalabrasBolt extends BaseRichBolt 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -505615409788951751L;
	private OutputCollector colector;

	/**
	 * El bolt recibe una linea de texto por llamada
	 * 
	 * Lo que hace es separar las palabras, quitarles
 	 * todo lo que no sean letras de la "a" a la "z"
 	 * y pasarlo todo a minúsculas
	 * 
	 */
	@Override
	public void execute(Tuple lineaEntrada)
	{
		//Creamos un array de palabras con la linea de entrada
		String linea = lineaEntrada.getString(0);
		String[] palabras = linea.split(" ");
		
		//Recorremos todas las palabras 
		//para pasarlas al siguiente bolt
		for(String palabra: palabras)
		{
			//"Limpiamos" la palabra de espacios, caracteres y números
			palabra = palabra.toString().replaceAll("[^A-Za-z\\s]", "");
			
			if(!palabra.isEmpty())
			{
				palabra = palabra.toLowerCase();
				
				//Emitir la palabra al siguiente bolt
				this.colector.emit(new Values(palabra));
			}
		}
		
		//Ack a la tupla
		colector.ack(lineaEntrada);
	}

	/**
	 * Este método se llama el primero dentro de la clase
	 * Prepara el bolt para los datos que va a tener que usar
	 */
	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map map, TopologyContext ctx, OutputCollector colector)
	{
		this.colector = colector;
	}

	/**
	 * 
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarador)
	{
		declarador.declare(new Fields("palabra"));
	}

}
