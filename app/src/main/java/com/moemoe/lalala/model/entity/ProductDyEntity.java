package com.moemoe.lalala.model.entity;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ProductDyEntity implements Parcelable {
	private String productId;
	private String productName;
	private String icon;
	private String describe;
	private int coin;
	private int rmb;

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ProductDyEntity> CREATOR = new Creator<ProductDyEntity>() {
		@Override
		public ProductDyEntity createFromParcel(Parcel parcel) {
			ProductDyEntity info = new ProductDyEntity();
			Bundle bundle = parcel.readBundle(getClass().getClassLoader());
			info.productId = bundle.getString("productId");
			info.productName = bundle.getString("productName");
			info.icon = bundle.getString("icon");
			info.describe = bundle.getString("describe");
			info.coin = bundle.getInt("coin");
			info.rmb = bundle.getInt("rmb");
			return info;
		}

		@Override
		public ProductDyEntity[] newArray(int i) {
			return new ProductDyEntity[0];
		}
	};

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		Bundle bundle = new Bundle();
		bundle.putString("productId",productId);
		bundle.putString("productName",productName);
		bundle.putString("icon",icon);
		bundle.putString("describe",describe);
		bundle.putInt("coin",coin);
		bundle.putInt("rmb",rmb);
		parcel.writeBundle(bundle);
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getRmb() {
		return rmb;
	}

	public void setRmb(int rmb) {
		this.rmb = rmb;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
