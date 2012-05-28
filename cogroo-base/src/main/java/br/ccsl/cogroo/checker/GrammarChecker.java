package br.ccsl.cogroo.checker;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import br.ccsl.cogroo.analyzer.ComponentFactory;
import br.ccsl.cogroo.analyzer.Pipe;
import br.ccsl.cogroo.text.Document;

/**
 * 
 */
public class GrammarChecker {
  /**
   * @param args
   *          the language to be used, "pt_BR" by default
   * @throws IOException
   * @throws IllegalArgumentException
   */
  public static void main(String[] args) throws IllegalArgumentException,
      IOException {

    long start = System.nanoTime();

    if (args.length != 1) {
      System.err.println("Language is missing! usage: CLI pt_br");
      return;
    }

    ComponentFactory factory = ComponentFactory.create(new Locale(args[0]));

    Pipe pipe = (Pipe) factory.createPipe();
    pipe.add(new GrammarCheckerAnalyzer());

    System.out.println("Loading time ["
        + ((System.nanoTime() - start) / 1000000) + "ms]");
    Scanner kb = new Scanner(System.in);
    System.out.print("Enter the sentence: ");
    String input = kb.nextLine();

    while (!input.equals("q")) {
      if (input.equals("0")) {
        input = "Pesquisa Datafolha publicada hoje revela um dado supreendente: recusando uma postura radical, a esmagadora maioria (77%) dos eleitores quer o PT participando do Governo Fernando Henrique Cardoso.";
      }

      Document document = new CheckDocument();
      document.setText(input);
      pipe.analyze(document);

      System.out.print("Enter the sentence: ");
      input = kb.nextLine();
    }
  }
}