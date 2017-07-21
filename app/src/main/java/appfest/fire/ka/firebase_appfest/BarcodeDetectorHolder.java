/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package appfest.fire.ka.firebase_appfest;

import android.content.Context;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

class BarcodeDetectorHolder {
  private static BarcodeDetector detector;

  static BarcodeDetector getBarcodeDetector(Context context) {
    if (detector == null) {
      detector = new BarcodeDetector.Builder(context.getApplicationContext()).setBarcodeFormats(
          Barcode.QR_CODE).build();
    }
    return detector;
  }
}

