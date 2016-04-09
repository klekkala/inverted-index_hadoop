package spouts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class LectorLineasSpout extends BaseRichSpout
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6785329165603525275L;
	private boolean completado = false;
	private FileReader lectorFichero;
	private SpoutOutputCollector colector;
	private TopologyContext ctx;

	/**
	 * Este es el primer método que se llama en el spout
	 * 
	 * En este caso se encarga de abrir el archivo del que
	 * vamos a leer
	 */
	@Override
	public void open(@SuppressWarnings("rawtypes") Map map, TopologyContext ctx, 
			SpoutOutputCollector colector) 
	{
		try 
		{
			this.setCtx(ctx);
			
			//Creamos un lector de ficheros
			this.lectorFichero = new FileReader(
					map.get("archivoPalabras").toString());
		} catch (FileNotFoundException e) {
			//Bloque de ejecución en caso de que el archivo
			//no exista o haya algún error en la apertura
			throw new RuntimeException(
					"Error reading file [" + map.get("wordFile")+"]"
			);
		}
		this.colector = colector;
	}

	/**
	 * Este método es el que lee cada línea del fichero
	 * de entrada y se encarga de emitirlas para ser
	 * procesadas por los bolt
	 */
	@Override
	public void nextTuple() 
	{
		//Comprueba si el fichero se ha terminado de leer
		if(this.completado)
		{
			//Esperar antes de intentar leer el fichero de nuevo
			try { Thread.sleep(1000);}
			catch (InterruptedException e) {}
		}
		
		//Creamos un lector de ficheros para leer linea por linea
		String linea;
		BufferedReader lector = new BufferedReader(this.lectorFichero);
		try 
		{
			while((linea = lector.readLine()) != null)
			{
				//Emitimos cada linea por el colector para
				//que los bolts las procesen
				this.colector.emit(new Values(linea), linea);
			}
		} catch (Exception e){
			throw new RuntimeException("Error leyendo", e);
		} finally {
			//Indicamos que el fichero se ha terminado de leer
			completado = true;
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarador)
	{
		declarador.declare(new Fields("linea"));
	}

	public TopologyContext getCtx() {
		return ctx;
	}

	public void setCtx(TopologyContext ctx) {
		this.ctx = ctx;
	}

}
