package com.kkmcn.sensordemo;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAccSensorValue;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketEddyTLM;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketIBeacon;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketSensor;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBeacon;


public class LeDeviceListAdapter extends BaseAdapter {

	// Adapter for holding devices found through scanning.
	public interface ListDataSource {
		KBeacon getBeaconDevice(int nIndex);

		int getCount();
	}

	private ListDataSource mDataSource;
	private Context mContext;

	public LeDeviceListAdapter(ListDataSource c, Context ctx) {
		super();
		mDataSource = c;
		mContext = ctx;
	}

	@Override
	public int getCount() {
		return mDataSource.getCount();
	}

	@Override
	public Object getItem(int i) {
		return mDataSource.getBeaconDevice(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}


	@Override
	public View getView(int i, View view, ViewGroup viewGroup)
	{
		ViewHolder viewHolder;
		// General ListView optimization code.
		if (view == null)
		{
			view = LayoutInflater.from(mContext).inflate(R.layout.listitem_device, null);
			viewHolder = new ViewHolder();
			viewHolder.deviceName = view
					.findViewById(R.id.beacon_name);

			viewHolder.deviceMacAddr =  view
					.findViewById(R.id.beacon_mac_address);

			viewHolder.rssiState = view
					.findViewById(R.id.beacon_rssi);

			viewHolder.deviceBatteryPercent = view
					.findViewById(R.id.beacon_battery_percent);

			//tlm
			viewHolder.llEddyTLM = view
					.findViewById(R.id.ll_eddy_tlm);
			viewHolder.deviceEddyTLM = view
					.findViewById(R.id.tv_tlm_beacon);

			//iBeacon uuid
			viewHolder.lliBeaconUUID= view
					.findViewById(R.id.ll_iBeacon_uuid);
			viewHolder.deviceIBeaconUUID = view
					.findViewById(R.id.tv_ibeacon_uuid);

			//iBeacon major
			viewHolder.lliBeaconMajor= view
					.findViewById(R.id.ll_iBeacon_major);
			viewHolder.deviceIBeaconMajor = view
					.findViewById(R.id.tv_iBeacon_major);


			//humidity
			viewHolder.llHTSensor= view
					.findViewById(R.id.ll_ht_sensor);
			viewHolder.deviceHumidity = view
					.findViewById(R.id.tv_ht_sensor);

			//acc sensor
			viewHolder.llAccSensor= view
					.findViewById(R.id.ll_acc_sensor);
			viewHolder.deviceAccPosition = view
					.findViewById(R.id.tv_acc_sensor);

			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) view.getTag();
		}

		KBeacon device = mDataSource.getBeaconDevice(i);
		if (device == null) {
			return null;
		}

		if (device.getName() != null && device.getName().length() > 0) {
			viewHolder.deviceName.setText(device.getName());
		}

		//common field
		String strMacAddress = mContext.getString(R.string.BEACON_MAC_ADDRESS) + device.getMac();
		viewHolder.deviceMacAddr.setText(strMacAddress);

		String strRssiValue = mContext.getString(R.string.BEACON_RSSI_VALUE) + device.getRssi();
		viewHolder.rssiState.setText(strRssiValue);

		String strBattPercent = mContext.getString(R.string.BEACON_BATTERY) + device.getBatteryPercent() + "%";
		viewHolder.deviceBatteryPercent.setText(strBattPercent);

		String strNA = "N/A";

		//ibeacon data
		KBAdvPacketIBeacon iBeaconAdv = (KBAdvPacketIBeacon) device.getAdvPacketByType(KBAdvType.IBeacon);
		if (iBeaconAdv != null)
		{
			viewHolder.lliBeaconMajor.setVisibility(View.VISIBLE);
			viewHolder.deviceIBeaconUUID.setVisibility(View.VISIBLE);

			//battery percent
			String strUUID = mContext.getString(R.string.BEACON_UUID) + iBeaconAdv.getUuid();

			String strMajor2Minor = mContext.getString(R.string.BEACON_MAJOR) + iBeaconAdv.getMajorID() +
					", " + mContext.getString(R.string.BEACON_MINOR) + iBeaconAdv.getMinorID();

			viewHolder.deviceIBeaconUUID.setText(strUUID);
			viewHolder.deviceIBeaconMajor.setText(strMajor2Minor);
		}
		else
		{
			viewHolder.lliBeaconMajor.setVisibility(View.GONE);
			viewHolder.deviceIBeaconUUID.setVisibility(View.GONE);
		}

		//tlm info
		KBAdvPacketEddyTLM eddyTLMAdv = (KBAdvPacketEddyTLM) device.getAdvPacketByType(KBAdvType.EddyTLM);
		if (eddyTLMAdv != null)
		{
			StringBuffer strTLMInfo = new StringBuffer(50);

			//battery voltage
			strTLMInfo.append(mContext.getString(R.string.BEACON_VOLTAGE))
					.append(eddyTLMAdv.getBatteryLevel())
					.append("mV, ");

			//temperature
			strTLMInfo.append(mContext.getString(R.string.BEACON_TEMPERATURE))
					.append(eddyTLMAdv.getTemperature())
					.append("℃, ");

			//adv count
			strTLMInfo.append("advCount:")
					.append(eddyTLMAdv.getAdvCount())
					.append(", ");

			//elapse 10 ms
			strTLMInfo.append("elapse:")
					.append(eddyTLMAdv.getSecCount());

			viewHolder.llEddyTLM.setVisibility(View.VISIBLE);
			viewHolder.deviceEddyTLM.setText(strTLMInfo.toString());
		}
		else
		{
			viewHolder.llEddyTLM.setVisibility(View.GONE);
		}

		//KBSensor info
		KBAdvPacketSensor kSensor = (KBAdvPacketSensor) device.getAdvPacketByType(KBAdvType.Sensor);
		if (kSensor != null)
		{
			//humidity and temp info
			StringBuffer strHTInfo = new StringBuffer(50);
			if (kSensor.getTemperature() != null)
			{
				strHTInfo.append(mContext.getString(R.string.BEACON_TEMPERATURE))
						.append(kSensor.getTemperature())
						.append("℃, ");
			}
			if (kSensor.getHumidity() != null)
			{
				strHTInfo.append(mContext.getString(R.string.BEACON_HUM))
						.append(kSensor.getHumidity())
						.append("%");
			}
			if (strHTInfo.length() > 1) {
				viewHolder.llHTSensor.setVisibility(View.VISIBLE);
				viewHolder.deviceHumidity.setText(strHTInfo.toString());
			}else{
				viewHolder.llHTSensor.setVisibility(View.GONE);
			}

			//acc sensor info
			KBAccSensorValue accSensorValue = kSensor.getAccSensor();
			if (accSensorValue != null)
			{
				String strAccAxis = mContext.getString(R.string.BEACON_ACC_POS) + "x=" + accSensorValue.xAis
						+ ",y=" + accSensorValue.yAis + ",z=" + accSensorValue.zAis;
				viewHolder.llAccSensor.setVisibility(View.VISIBLE);
				viewHolder.deviceAccPosition.setText(strAccAxis);
			}
			else
			{
				viewHolder.llAccSensor.setVisibility(View.GONE);
			}
		}
		else
		{
			viewHolder.llHTSensor.setVisibility(View.GONE);
			viewHolder.llAccSensor.setVisibility(View.GONE);
		}

		return view;
	}

	class ViewHolder {
		TextView deviceName;      //名称

		TextView rssiState;     //状态
		TextView deviceBatteryPercent;
		TextView deviceMacAddr;

		LinearLayout lliBeaconUUID;
		LinearLayout lliBeaconMajor;

		LinearLayout llHTSensor;
		LinearLayout llAccSensor;
		LinearLayout llEddyTLM;

		TextView deviceIBeaconUUID;
		TextView deviceIBeaconMajor;

		TextView deviceEddyTLM;
		TextView deviceHumidity;
		TextView deviceAccPosition;
	}
}
