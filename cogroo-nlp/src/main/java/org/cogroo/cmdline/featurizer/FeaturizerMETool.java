/**
 * Copyright (C) 2012 cogroo <cogroo@cogroo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogroo.cmdline.featurizer;

import java.io.File;
import java.io.IOException;

import org.cogroo.tools.featurizer.FeatureSample;
import org.cogroo.tools.featurizer.FeaturizerME;
import org.cogroo.tools.featurizer.FeaturizerModel;

import opennlp.tools.cmdline.BasicCmdLineTool;
import opennlp.tools.cmdline.CLI;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.SystemInputStreamFactory;
import opennlp.tools.postag.POSSample;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class FeaturizerMETool extends BasicCmdLineTool {

  public String getShortDescription() {
    return "learnable Featurizer";
  }

  public String getHelp() {
    return "Usage: " + CLI.CMD + " " + getName() + " model < sentences";
  }

  public void run(String[] args) {
    if (args.length != 1) {
      System.out.println(getHelp());
    } else {
      FeaturizerModel model = new FeaturizerModelLoader()
          .load(new File(args[0]));

      FeaturizerME Featurizer = new FeaturizerME(model);

      ObjectStream<String>  lineStream = null;
      try {
        lineStream = new PlainTextByLineStream(new SystemInputStreamFactory(),
            SystemInputStreamFactory.encoding());
      } catch (IOException e) {
        CmdLineUtil.handleStdinIoError(e);
      }

      PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
      perfMon.start();

      try {
        String line;
        while ((line = lineStream.read()) != null) {

          POSSample posSample;
          try {
            posSample = POSSample.parse(line);
          } catch (InvalidFormatException e) {
            System.err.println("Invalid format:");
            System.err.println(line);
            continue;
          }

          String[] feats = Featurizer.featurize(posSample.getSentence(),
              posSample.getTags());

          String[] empty = new String[feats.length];

          System.out.println(new FeatureSample(posSample.getSentence(), empty,
              posSample.getTags(), feats).toString());

          perfMon.incrementCounter();
        }
      } catch (IOException e) {
        CmdLineUtil.handleStdinIoError(e);
      }

      perfMon.stopAndPrintFinalResult();
    }
  }
}
