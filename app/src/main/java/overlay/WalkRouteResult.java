//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package overlay;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.amap.api.services.route.RouteResult;
import overlay.WalkPath;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import java.util.ArrayList;
import java.util.List;

public class WalkRouteResult extends RouteResult implements Parcelable {
	private List<WalkPath> a = new ArrayList();
	private WalkRouteQuery b;
	public static final Creator<WalkRouteResult> CREATOR = new Creator() {
		public WalkRouteResult a(Parcel var1) {
			return new WalkRouteResult(var1);
		}

		public WalkRouteResult[] a(int var1) {
			return new WalkRouteResult[var1];
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

	public List<WalkPath> getPaths() {
		return this.a;
	}

	public void setPaths(List<WalkPath> var1) {
		this.a = var1;
	}

	public WalkRouteQuery getWalkQuery() {
		return this.b;
	}

	public void setWalkQuery(WalkRouteQuery var1) {
		this.b = var1;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel var1, int var2) {
		super.writeToParcel(var1, var2);
		var1.writeTypedList(this.a);
		var1.writeParcelable(this.b, var2);
	}

	public WalkRouteResult(Parcel var1) {
		super(var1);
		this.a = var1.createTypedArrayList(WalkPath.CREATOR);
		this.b = (WalkRouteQuery)var1.readParcelable(WalkRouteQuery.class.getClassLoader());
	}

	public WalkRouteResult() {
	}
}
