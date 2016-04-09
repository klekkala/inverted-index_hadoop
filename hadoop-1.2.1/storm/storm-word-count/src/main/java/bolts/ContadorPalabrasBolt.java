package bolts;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class ContadorPalabrasBolt extends BaseRichBolt 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9211454481547491256L;
	private String nombre;
	private Integer id;
	private OutputCollector colector;
	private Map<String,Integer> cuenta;
	
	@Override
	public void execute(Tuple palabraEntrada) 
	{
		String palabra = palabraEntrada.getString(0);
		
		/**
		* Comprueba si existe ya la palabra en el Map
		* y la crea si no existe todavia
		*/
		if(!this.cuenta.containsKey(palabra))
		{
			this.cuenta.put(palabra, 1);
		} else {
			Integer c = this.cuenta.get(palabra) + 1;
			this.cuenta.put(palabra, c);
		}
		
		//Ack a la tupla
		colector.ack(palabraEntrada);
	}

	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map map, TopologyContext ctx, OutputCollector colector)
	{
		this.cuenta = new HashMap<String, Integer>();
		this.colector = colector;
		
		this.nombre = ctx.getThisComponentId();
		this.id = ctx.getThisTaskId();
	}

	/**
	 * Al finalizar el Spout, mostrar por consola la
	 * cuenta de palabras
	 */
	@Override
	public void cleanup() {
		super.cleanup();
		System.out.println("[WORDCOUNTBOLT]-- Contador palabras [" + nombre + "-" + id + "] --");

		Logger logger = LoggerFactory.getLogger(getClass());
		
		for(Map.Entry<String, Integer> resultado: cuenta.entrySet())
		{
			System.out.println("[WORDCOUNTBOLT]" + resultado.getKey() + ", " + resultado.getValue());
			logger.info("[WORDCOUNTBOLT]" + resultado.getKey() + ", " + resultado.getValue());
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarador)
	{
		//Ultimo bolt que no llega a emitir nada mas
	}

}
