package com.mariocastro.wordcount;

import java.io.*;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * Contador de Palabras MapReduce mejorado
 * @author Mario Caster
 * @email mariocaster@gmail.com
 */
public class Wordcount
{
  // Entrada de la aplicación
  public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException 
  {
    //Creamos un fichero de configuracion y le damos un nombre al Job
    Configuration conf = new Configuration();
    Job job = new Job(conf, "word count");

    //Le tenemos que indicar la clase que hay que usar para llamar a los mappers y reducers
    job.setJarByClass(Wordcount.class);

    //Le indicamos el nombre de la clase Mapper y de la clase Reducer
    job.setMapperClass(WordCountMapper.class);
    job.setReducerClass(WordCountReducer.class);

    /* Le tenemos que indicar el formato que va a tener el resultado, en nuestro caso vamos
     * a recuperar un resultado del tipo <palabra, numero_de_ocurrencias> por lo que, usando
     * los tipos primitivos de Hadoop, esto equivaldría a Text.class y IntWritable.class
     */
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    /* Le indicamos el fichero de entrada y de salida que, por lo general, los vamos a recoger
     * de los parámetros que le pasemos a la clase JAR
     */
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    //Esperamos a que el trabajo termine
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
  /**
   *  Mapper.
   *  Recibirá, línea tras línea (el objeto Text), los contenidos del fichero que
   *  le pasemos por los argumentos.
   *
   *  Vamos a mejorar el Mapper inicial quitando todos los caracteres no alfabéticos
   *  de la cadena de entrada para evitar que, por ejemplo, "casa" y "casa." se
   *  contabilicen como palabras distintas
   */
  public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    private Text word = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException 
    {
      //Escribimos una expresión regular para eliminar todo caracter no alfabético
      String cleanString = value.toString().replaceAll("[^A-Za-z]", "");

      //Ahora cogemos el texto limpio y lo separamos por espacios
      StringTokenizer token = new StringTokenizer(cleanString);
      
      //Recorremos el tokenizer para recoger todas las palabras hasta que no haya mas
      while(token.hasMoreTokens())
      {
        //Palabra actual
        String tok = token.nextToken();

        //Asignar la palabra al objeto que se va a pasar al reducer
        word.set(tok);

        /*
         * Guardar la palabra con un valor número de 1 de tal manera que, si tenemos
         * la palabra "casa" estamos guardando <casa, [1]> Si la palabra casa volviera
         * a aparecer, como ya la hemos establecido una vez, el resultado sería 
         * <casa, [1,1]> y así por cada ocurrencia
         */
        context.write(word, new IntWritable(1));
      }
    }
  }

  /*
   * El reducer va a contar el número de ítems en la lista de palabras que pasamos desde el
   * Mapper
   */
  public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text word, Iterable<IntWritable> list, Context context)
        throws IOException, InterruptedException
    {
      //Ponemos el contador de palabras a 0
      int total = 0;
      
      //Recorremos el objeto Iterable<IntWritable> list y sumamos uno al contador
      for(IntWritable count : list)
      {
        total++;
      }
      
      /* Escribimos el resultado del reducer, para el ejemplo de casa, escribiría
       * <casa,2>
       */
      context.write(word, new IntWritable(total));
    }
    
  }
}