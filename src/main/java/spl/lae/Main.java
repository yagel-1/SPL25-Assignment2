package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
      // TODO: main
      if (args.length < 3) {
            System.out.println("Error: Missing arguments.");
            return;
        }

      int numOfThreads = 0;
      try {
          numOfThreads = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
          System.out.println("Error: First argument must be an integer (number of threads).");
          return;
      }
      String inputPathString = args[1];
      String outputPathString = args[2];

      LinearAlgebraEngine lae = new LinearAlgebraEngine(numOfThreads);
      InputParser parser = new InputParser();

      try{
        ComputationNode computationRoot = parser.parse(inputPathString);
        ComputationNode result = lae.run(computationRoot);
        OutputWriter.write(result.getMatrix(), outputPathString);
      } catch(Exception errorMsg){
        try{
          OutputWriter.write(errorMsg.getMessage(), outputPathString);
        } catch (IOException ex) {
          System.out.println("Critical error: Failed to write error message to file.");
        }
      }
    }
}