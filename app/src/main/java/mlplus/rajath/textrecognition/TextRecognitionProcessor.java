// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package mlplus.rajath.textrecognition;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.IOException;
import java.util.List;

import mlplus.rajath.FrameMetadata;
import mlplus.rajath.GraphicOverlay;
import mlplus.rajath.VisionProcessorBase;

/** Processor for the text recognition demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

  Context context;

  private static final String TAG = "TextRecProc";

  private final FirebaseVisionTextRecognizer detector;

  public TextRecognitionProcessor(Context context) {
    detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    this.context = context;
  }

  @Override
  public void stop() {
    try {
      detector.close();
    } catch (IOException e) {
      Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
    }
  }

  @Override
  protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
    return detector.processImage(image);
  }

  @Override
  protected void onSuccess(
      @NonNull FirebaseVisionText results,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {
    graphicOverlay.clear();
    List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
    for (int i = 0; i < blocks.size(); i++) {
      List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
      for (int j = 0; j < lines.size(); j++) {
        List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
        for (int k = 0; k < elements.size(); k++) {
          GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
          graphicOverlay.add(textGraphic);

        }
      }
    }


    String resultText = results.getText();
    for (FirebaseVisionText.TextBlock block: results.getTextBlocks()) {
      String blockText = block.getText();
      Float blockConfidence = block.getConfidence();
      List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
      Point[] blockCornerPoints = block.getCornerPoints();
      Rect blockFrame = block.getBoundingBox();
      for (FirebaseVisionText.Line line: block.getLines()) {
        String lineText = line.getText();
        Float lineConfidence = line.getConfidence();
        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
        Point[] lineCornerPoints = line.getCornerPoints();
        Rect lineFrame = line.getBoundingBox();
        for (FirebaseVisionText.Element element: line.getElements()) {
          String elementText = element.getText();
          Float elementConfidence = element.getConfidence();
          List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
          Point[] elementCornerPoints = element.getCornerPoints();
          Rect elementFrame = element.getBoundingBox();
        }
      }
    }
    Log.e("Text",resultText);
    Toast.makeText(context, resultText, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}
