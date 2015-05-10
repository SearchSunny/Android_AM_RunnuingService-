package com.qin.runservice;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//自定义适配器类，提供给listView的自定义view
public class BrowseRunningServiceAdapter extends BaseAdapter {
	
	private List<RunSericeModel> mlistAppInfo = null;
	
	LayoutInflater infater = null;
    
	public BrowseRunningServiceAdapter(Context context,  List<RunSericeModel> apps) {
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistAppInfo = apps ;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("size" + mlistAppInfo.size());
		return mlistAppInfo.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistAppInfo.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		System.out.println("getView at " + position);
		View view = null;
		ViewHolder holder = null;
		if (convertview == null || convertview.getTag() == null) {
			view = infater.inflate(R.layout.browse_service_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} 
		else{
			view = convertview ;
			holder = (ViewHolder) convertview.getTag() ;
		}
		RunSericeModel runServiceInfo = (RunSericeModel) getItem(position);
		holder.appIcon.setImageDrawable(runServiceInfo.getAppIcon());
		holder.tvAppLabel.setText(runServiceInfo.getAppLabel());
		holder.tvServiceName.setText(runServiceInfo.getServiceName());
		holder.tvProcessId.setText(runServiceInfo.getPid()+"") ;
		holder.tvProcessName.setText(runServiceInfo.getProcessName()) ;
		
		return view;
	}

	class ViewHolder {
		ImageView appIcon;
		TextView tvAppLabel ;
		TextView tvServiceName;
        TextView tvProcessId ;
		TextView tvProcessName ;
        
        
		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.imgApp);
			this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
			this.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);
			this.tvProcessId = (TextView) view.findViewById(R.id.tvProcessId);
			this.tvProcessName = (TextView) view.findViewById(R.id.tvProcessName);
		}
	}
}