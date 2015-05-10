package com.qin.runservice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BrowseRunningServiceActivity extends Activity implements
		OnItemClickListener {

	private static String TAG = "RunServiceInfo";

	private ActivityManager mActivityManager = null;
	// ProcessInfo Model�� �����������н�����Ϣ
	private List<RunSericeModel> serviceInfoList = null;

	private ListView listviewService;
	private TextView tvTotalServiceNo;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.browse_service_list);

		listviewService = (ListView) findViewById(R.id.listviewService);
		listviewService.setOnItemClickListener(this);

		tvTotalServiceNo = (TextView) findViewById(R.id.tvTotalServiceNo);

		// ���ActivityManager����Ķ���
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		// ����������е�Service��Ϣ
		getRunningServiceInfo();
		// �Լ�������
		Collections.sort(serviceInfoList, new comparatorServiceLable());

		System.out.println(serviceInfoList.size() + "-------------");

		// ΪListView��������������
		BrowseRunningServiceAdapter mServiceInfoAdapter = new BrowseRunningServiceAdapter(BrowseRunningServiceActivity.this, serviceInfoList);

		listviewService.setAdapter(mServiceInfoAdapter);

		tvTotalServiceNo.setText("��ǰ�������еķ����У�" + serviceInfoList.size());
	}
	// ���ϵͳ�������еĽ�����Ϣ
	private void getRunningServiceInfo() {

		// ����һ��Ĭ��Service��������С
		int defaultNum = 20;
		// ͨ������ActivityManager��getRunningAppServicees()�������ϵͳ�������������еĽ���
		List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager
				.getRunningServices(defaultNum);

		System.out.println(runServiceList.size());

		// ServiceInfo Model�� �����������н�����Ϣ
		serviceInfoList = new ArrayList<RunSericeModel>();

		for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {

			// ���Service���ڵĽ��̵���Ϣ
			int pid = runServiceInfo.pid; // service���ڵĽ���ID��
			int uid = runServiceInfo.uid; // �û�ID ������Linux��Ȩ�޲�ͬ��IDҲ�Ͳ�ͬ ���� root��
			// ��������Ĭ���ǰ�������������android��processָ��
			String processName = runServiceInfo.process; 

			// ��Service����ʱ��ʱ��ֵ
			long activeSince = runServiceInfo.activeSince;

			// �����Service��ͨ��Bind������ʽ���ӣ���clientCount������service���ӿͻ��˵���Ŀ
			int clientCount = runServiceInfo.clientCount;

			// ��ø�Service�������Ϣ ������pkgname/servicename
			ComponentName serviceCMP = runServiceInfo.service;
			String serviceName = serviceCMP.getShortClassName(); // service ������
			String pkgName = serviceCMP.getPackageName(); // ����

			// ��ӡLog
			Log.i(TAG, "���ڽ���id :" + pid + " ���ڽ�������" + processName + " ���ڽ���uid:"
					+ uid + "\n" + " service������ʱ��ֵ��" + activeSince
					+ " �ͻ��˰���Ŀ:" + clientCount + "\n" + "��service�������Ϣ:"
					+ serviceName + " and " + pkgName);

			// �������ͨ��service�������Ϣ������PackageManager��ȡ��service����Ӧ�ó���İ��� ��ͼ���
			PackageManager mPackageManager = this.getPackageManager(); // ��ȡPackagerManager����;

			try {
				// ��ȡ��pkgName����Ϣ
				ApplicationInfo appInfo = mPackageManager.getApplicationInfo(
						pkgName, 0);

				RunSericeModel runService = new RunSericeModel();
				runService.setAppIcon(appInfo.loadIcon(mPackageManager));
				runService.setAppLabel(appInfo.loadLabel(mPackageManager) + "");
				runService.setServiceName(serviceName);
				runService.setPkgName(pkgName);
				// ���ø�service�������Ϣ
				Intent intent = new Intent();
				intent.setComponent(serviceCMP);
				runService.setIntent(intent);

				runService.setPid(pid);
				runService.setProcessName(processName);

				// �����������
				serviceInfoList.add(runService);

			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("--------------------- error -------------");
				e.printStackTrace();
			}

		}
	}

	// ������ֹͣ
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		final Intent stopserviceIntent = serviceInfoList.get(position)
				.getIntent();

		new AlertDialog.Builder(BrowseRunningServiceActivity.this).setTitle(
				"�Ƿ�ֹͣ����").setMessage(
				"����ֻ�������������󣬲ſ��Լ������С�������ܻ�������г�Ӧ�ó���������벻���Ľ����")
				.setPositiveButton("ֹͣ", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// ֹͣ��Service
						//����Ȩ�޲��������⣬Ϊ�˱���Ӧ�ó�������쳣�������SecurityException ���������Ի���
						try {
							stopService(stopserviceIntent);
						} catch (SecurityException sEx) {
							//�����쳣 ˵��Ȩ�޲��� 
                            System.out.println(" deny the permission");
                        	new AlertDialog.Builder(BrowseRunningServiceActivity.this).setTitle(
            				"Ȩ�޲���").setMessage("�Բ�������Ȩ�޲������޷�ֹͣ��Service").create().show();
     					}
						// ˢ�½���
						// ����������е�Service��Ϣ
						getRunningServiceInfo();
						// �Լ�������
						Collections.sort(serviceInfoList, new comparatorServiceLable());
						// ΪListView��������������
						BrowseRunningServiceAdapter mServiceInfoAdapter = new BrowseRunningServiceAdapter(
								BrowseRunningServiceActivity.this,
								serviceInfoList);
						listviewService.setAdapter(mServiceInfoAdapter);
						tvTotalServiceNo.setText("��ǰ�������еķ����У�"
								+ serviceInfoList.size());
					}

				}).setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss(); // ȡ���Ի���
							}
						}).create().show();
	}

	// �Զ������� ����AppLabel����
	private class comparatorServiceLable implements Comparator<RunSericeModel> {

		@Override
		public int compare(RunSericeModel object1, RunSericeModel object2) {
			// TODO Auto-generated method stub
			return object1.getAppLabel().compareTo(object2.getAppLabel());
		}

	}

}
