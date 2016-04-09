package topology;

import spouts.LectorLineasSpout;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolts.ContadorPalabrasBolt;
import bolts.SeparadorPalabrasBolt;

public class FileWordCount
{
	public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException
	{
		//Definicion de la topología
		TopologyBuilder constructor = new TopologyBuilder();
		constructor.setSpout("lector-lineas", new LectorLineasSpout());
		constructor.setBolt("separador-palabras", new SeparadorPalabrasBolt())
			.shuffleGrouping("lector-lineas");
		constructor.setBolt("contador-palabras", new ContadorPalabrasBolt())
			.fieldsGrouping("separador-palabras", new Fields("palabra"));
		
		//Configuracion
		Config conf = new Config();
		conf.put("archivoPalabras", args[0]);
		conf.setDebug(false);
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		
		//Arrancar la topología
		/*LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(
				"Mi-primera-topología", 
				conf, 
				constructor.createTopology()
			);
		Thread.sleep(1000);
		cluster.shutdown();*/
		
		conf.setNumWorkers(5);
		conf.setMaxSpoutPending(1);
		StormSubmitter.submitTopology("mytopology", conf, constructor.createTopology());
	}
}
