package overlay;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.TMC;

import java.util.ArrayList;
import java.util.List;

public class WalkStep
		implements Parcelable
{
	private String a;
	private String b;
	private String c;
	private float d;
	private float e;
	private List<LatLonPoint> f = new ArrayList();
	private String g;
	private String h;
	private List<TMC> m = new ArrayList();

	public String getInstruction()
	{
		return this.a;
	}

	public void setInstruction(String paramString)
	{
		this.a = paramString;
	}

	public String getOrientation()
	{
		return this.b;
	}

	public void setOrientation(String paramString)
	{
		this.b = paramString;
	}

	public String getRoad()
	{
		return this.c;
	}

	public void setRoad(String paramString)
	{
		this.c = paramString;
	}

	public float getDistance()
	{
		return this.d;
	}

	public void setDistance(float paramFloat)
	{
		this.d = paramFloat;
	}

	public float getDuration()
	{
		return this.e;
	}

	public void setDuration(float paramFloat)
	{
		this.e = paramFloat;
	}

	public List<LatLonPoint> getPolyline()
	{
		return this.f;
	}

	public void setPolyline(List<LatLonPoint> paramList)
	{
		this.f = paramList;
	}

	public String getAction()
	{
		return this.g;
	}

	public void setAction(String paramString)
	{
		this.g = paramString;
	}

	public String getAssistantAction()
	{
		return this.h;
	}

	public void setAssistantAction(String paramString)
	{
		this.h = paramString;
	}

	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel paramParcel, int paramInt)
	{
		paramParcel.writeString(this.a);
		paramParcel.writeString(this.b);
		paramParcel.writeString(this.c);
		paramParcel.writeFloat(this.d);
		paramParcel.writeFloat(this.e);
		paramParcel.writeTypedList(this.f);
		paramParcel.writeString(this.g);
		paramParcel.writeString(this.h);
	}

	public WalkStep(Parcel paramParcel)
	{
		this.a = paramParcel.readString();
		this.b = paramParcel.readString();
		this.c = paramParcel.readString();
		this.d = paramParcel.readFloat();
		this.e = paramParcel.readFloat();
		this.f = paramParcel.createTypedArrayList(LatLonPoint.CREATOR);
		this.g = paramParcel.readString();
		this.h = paramParcel.readString();
	}

	public static final Parcelable.Creator<WalkStep> CREATOR = new Parcelable.Creator()
	{
		public WalkStep a(Parcel paramAnonymousParcel)
		{
			return new WalkStep(paramAnonymousParcel);
		}

		public WalkStep[] a(int paramAnonymousInt)
		{
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

	public WalkStep() {}
	public List<TMC> getTMCs() {
		return this.m;
	}

	public void setTMCs(List<TMC> var1) {
		this.m = var1;
	}

}
