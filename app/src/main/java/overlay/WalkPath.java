//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package overlay;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.amap.api.services.route.Path;
import overlay.WalkStep;
import java.util.ArrayList;
import java.util.List;

public class WalkPath extends Path implements Parcelable {
	private List<WalkStep> a = new ArrayList();
	public static final Creator CREATOR = new Creator() {
		public WalkPath a(Parcel var1) {
			return new WalkPath(var1);
		}

		public WalkPath[] a(int var1) {
			return null;
		}
		public WalkStep createFromParcel(Parcel source){
			WalkStep w=new WalkStep();
			return w;
		}
		public WalkStep[] newArray(int size){
			WalkStep[] w1=new WalkStep[size];
			return w1;
		}
	};

	public List<WalkStep> getSteps() {
		return this.a;
	}

	public void setSteps(List<WalkStep> var1) {
		this.a = var1;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel var1, int var2) {
		super.writeToParcel(var1, var2);
		var1.writeTypedList(this.a);
	}

	public WalkPath(Parcel var1) {
		super(var1);
		this.a = var1.createTypedArrayList(WalkStep.CREATOR);
	}

	public WalkPath() {
	}
}
