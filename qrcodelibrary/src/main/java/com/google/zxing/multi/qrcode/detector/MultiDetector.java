//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.multi.qrcode.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.multi.qrcode.detector.MultiFinderPatternFinder;
import com.google.zxing.qrcode.detector.Detector;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import java.util.Hashtable;
import java.util.Vector;

public final class MultiDetector extends Detector {
    private static final DetectorResult[] EMPTY_DETECTOR_RESULTS = new DetectorResult[0];

    public MultiDetector(BitMatrix image) {
        super(image);
    }

    public DetectorResult[] detectMulti(Hashtable hints) throws NotFoundException {
        BitMatrix image = this.getImage();
        MultiFinderPatternFinder finder = new MultiFinderPatternFinder(image);
        FinderPatternInfo[] info = finder.findMulti(hints);
        if(info != null && info.length != 0) {
            Vector result = new Vector();

            for(int resultArray = 0; resultArray < info.length; ++resultArray) {
                try {
                    result.addElement(this.processFinderPatternInfo(info[resultArray]));
                } catch (ReaderException var8) {
                    ;
                }
            }

            if(result.isEmpty()) {
                return EMPTY_DETECTOR_RESULTS;
            } else {
                DetectorResult[] var9 = new DetectorResult[result.size()];

                for(int i = 0; i < result.size(); ++i) {
                    var9[i] = (DetectorResult)result.elementAt(i);
                }

                return var9;
            }
        } else {
            throw NotFoundException.getNotFoundInstance();
        }
    }
}
