package cn.com.gfa.ware.baidu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cn.com.gfa.ware.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.CloudListener;
import com.baidu.mapapi.cloud.CloudManager;
import com.baidu.mapapi.cloud.CloudPoiInfo;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.cloud.DetailSearchResult;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.cloud.NearbySearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocationActivity extends Activity implements CloudListener,BaiduMap.OnMapClickListener,
OnGetRoutePlanResultListener,OnGetGeoCoderResultListener{

	private static final String TAG = LocationActivity.class.getSimpleName();
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
//	OnCheckedChangeListener radioButtonListener;
//	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	
	
	GeoCoder mGeo = null; // 搜索模块，也可去掉地图模块独立使用
	 RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
	 
	 int nodeIndex = -1;//节点索引,供浏览节点时使用
	 RouteLine route = null;
	 OverlayManager routeOverlay = null;
	 boolean useDefaultIcon = false;
	 
	 String mName,mRegion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		Intent intent=getIntent();
		mName = intent.getStringExtra("name");
		mRegion = intent.getStringExtra("region");
		
//		requestLocButton = (Button) findViewById(R.id.button1);
//		mCurrentMode = LocationMode.NORMAL;
//		requestLocButton.setText("普通");
//		OnClickListener btnClickListener = new OnClickListener() {
//			public void onClick(View v) {
//				switch (mCurrentMode) {
//				case NORMAL:
//					requestLocButton.setText("跟随");
//					mCurrentMode = LocationMode.FOLLOWING;
//					mBaiduMap
//							.setMyLocationConfigeration(new MyLocationConfiguration(
//									mCurrentMode, true, mCurrentMarker));
//					break;
//				case COMPASS:
//					requestLocButton.setText("普通");
//					mCurrentMode = LocationMode.NORMAL;
//					mBaiduMap
//							.setMyLocationConfigeration(new MyLocationConfiguration(
//									mCurrentMode, true, mCurrentMarker));
//					break;
//				case FOLLOWING:
//					requestLocButton.setText("罗盘");
//					mCurrentMode = LocationMode.COMPASS;
//					mBaiduMap
//							.setMyLocationConfigeration(new MyLocationConfiguration(
//									mCurrentMode, true, mCurrentMarker));
//					break;
//				}
//			}
//		};
//		requestLocButton.setOnClickListener(btnClickListener);

//		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
//		radioButtonListener = new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				if (checkedId == R.id.defaulticon) {
//					// 传入null则，恢复默认图标
//					mCurrentMarker = null;
//					mBaiduMap
//							.setMyLocationConfigeration(new MyLocationConfiguration(
//									mCurrentMode, true, null));
//				}
//				if (checkedId == R.id.customicon) {
//					// 修改为自定义marker
//					mCurrentMarker = BitmapDescriptorFactory
//							.fromResource(R.drawable.icon_geo);
//					mBaiduMap
//							.setMyLocationConfigeration(new MyLocationConfiguration(
//									mCurrentMode, true, mCurrentMarker));
//				}
//			}
//		};
//		group.setOnCheckedChangeListener(radioButtonListener);

		
		
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		
		
		
		
		
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		// 初始化搜索模块，注册事件监听
		mGeo = GeoCoder.newInstance();
		mGeo.setOnGetGeoCodeResultListener(this);
		CloudManager.getInstance().init(LocationActivity.this);
//		LocalSearchInfo info = new LocalSearchInfo();
//		info.ak = "QVE9iNrd5kfExC5PG0A2Er51";//"p63210Gut35HV9tuwjf6i6f3";
//		info.geoTableId = 125661;//125049;
//		info.tags = "";
//		info.q = "海关";
//		info.region = "北京市";
//		CloudManager.getInstance().localSearch(info);
		
		
		//地图点击事件处理
		mBaiduMap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
        
        
        //导航
        if ( initDirs() ) {
			initNavi();
		}
        
        
        
		//此方法能不能掉到最上面
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				if(marker.getTitle()!=null){
				Button button = new Button(LocationActivity.this);
				button.setBackgroundResource(R.drawable.gray);
//				OnInfoWindowClickListener listener = null;
					button.setAlpha(100);
					button.setText(marker.getTitle()+"   >");
					button.setTextColor(0xff666667);
//					button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
					button.setTextSize(14);

//					listener = new OnInfoWindowClickListener() {
//						public void onInfoWindowClick() {
//							LatLng ll = marker.getPosition();
//							Log.d(TAG, "setOnMarkerClickListener: "+marker.getTitle() + " : "+ ll.latitude + " ; " + ll.longitude);
//							SearchButtonProcess(ll);
//							
//							if ( BaiduNaviManager.isNaviInited() ) {
//								routeplanToNavi(CoordinateType.GCJ02, marker);//????????????
//							}
//							mBaiduMap.hideInfoWindow();
//						}
//					};
					button.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							LatLng ll = marker.getPosition();
							Log.d(TAG, "setOnMarkerClickListener: "+marker.getTitle() + " : "+ ll.latitude + " ; " + ll.longitude);
							SearchButtonProcess(ll);
							
							if ( BaiduNaviManager.isNaviInited() ) {
								routeplanToNavi(CoordinateType.GCJ02, marker);//导航位置不准????????????
							}
							mBaiduMap.hideInfoWindow();
						}
					});
					LatLng ll = marker.getPosition();
//					InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
					InfoWindow mInfoWindow = new InfoWindow(button, ll, -57);
					mBaiduMap.showInfoWindow(mInfoWindow);
					
//					TextView view = new TextView(getApplicationContext());
//					view.setText(marker.getTitle());
//					view.setTextColor(Color.BLACK);
//					
//					LinearLayout layout = new LinearLayout(getApplicationContext());
//					layout.setOrientation(LinearLayout.VERTICAL);
//					layout.setBackgroundResource(R.drawable.popup);
//					layout.addView(button);
//					layout.addView(view);
////					button.setTextSize(12);
////					button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
////			                LinearLayout.LayoutParams.WRAP_CONTENT));
//					
//					InfoWindow mInfoWindow = new InfoWindow(layout, ll, -47);
//					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				return true;
			}
		});
	}
	
	
	
	private String mSDCardPath = null;
	String authinfo = null;
	private static final String APP_FOLDER_NAME = "BNSDKDemo";
	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	
	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if ( mSDCardPath == null ) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if ( !f.exists() ) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	private void initNavi() {
	    BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath + "/BaiduNaviSDK_SO");
		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
			new NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                LocationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(LocationActivity.this, authinfo, Toast.LENGTH_LONG).show();
                    }
                });

            }
				public void initSuccess() {
					Toast.makeText(LocationActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
				}
				
				public void initStart() {
					Toast.makeText(LocationActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
				}
				
				public void initFailed() {
					Toast.makeText(LocationActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
				}
		}, null /*mTTSCallback*/);
	}
	
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private BDLocation local = null;
	private AddressComponent localDetail = null;
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			Log.d(TAG, "onReceiveLocation:========================="+isFirstLoc);
//			mCurrentMode = LocationMode.NORMAL;
//			mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
//			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
//							mCurrentMode, true, mCurrentMarker));
			
			local = location;
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				
				//117.339656,36.558512
				mGeo.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
				
				mBaiduMap.animateMapStatus(u);
				
			}
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		
		mGeo.destroy();
		mSearch.destroy();
		
		
		CloudManager.getInstance().destroy();
	}

	public void onGetDetailSearchResult(DetailSearchResult result, int error) {
		Log.d(TAG, "=================================onGetDetailSearchResult======================== ");
		if (result != null) {
			if (result.poiInfo != null) {
				Toast.makeText(LocationActivity.this, result.poiInfo.title,
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(LocationActivity.this,
						"status:" + result.status, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onGetSearchResult(CloudSearchResult result, int error) {
		Log.d(TAG, "onGetSearchResult, result length=========: " + result.poiList.size());
		if (result != null && result.poiList != null
				&& result.poiList.size() > 0) {
			Log.d(TAG, "onGetSearchResult, result length: " + result.poiList.size());
			mBaiduMap.clear();
			BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
			LatLng ll;
			LatLngBounds.Builder builder = new Builder();
			ll = new LatLng(local.getLatitude(), local.getLongitude());
			builder.include(ll);
			for (CloudPoiInfo info : result.poiList) {
				ll = new LatLng(info.latitude, info.longitude);
				OverlayOptions oo = new MarkerOptions().icon(bd).position(ll).title(info.title);
				mBaiduMap.addOverlay(oo);
				builder.include(ll);
				
//				ImageView view = new ImageView(LocationActivity.this);
//				view.setImageResource(R.drawable.mark_bule);
//				BitmapDescriptor bdm = BitmapDescriptorFactory.fromResource(R.drawable.mark_bule);
//				LatLng llm = new LatLng(info.latitude-0.004, info.longitude+0.004);
//				OverlayOptions oom = new MarkerOptions().icon(bdm).position(llm);
//				Marker mark = (Marker)mBaiduMap.addOverlay(oom);
//				builder.include(llm);
				Log.d(TAG, "onGetSearchResult: " +info.title + " : "+ ll.latitude + " ; " + ll.longitude);
			}
			LatLngBounds bounds = builder.build();
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
			mBaiduMap.animateMapStatus(u);
			

		}
	}
	
	
	
	public void SearchButtonProcess(LatLng ll) {
        //重置浏览节点的路线数据
		route = null;
		//mBaiduMap.clear();
        //设置起终点信息，对于tranist search 来说，城市名无意义
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("北京", "龙泽");
//        PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", "西单");
		LatLng start = new LatLng(local.getLatitude(), local.getLongitude());
		PlanNode stNode = PlanNode.withLocation(start);
		PlanNode enNode = PlanNode.withLocation(ll);
        // 实际使用中请对起点终点城市进行正确的设定
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
       
    }
	
	
	
	private void routeplanToNavi(CoordinateType coType , Marker mark) {
	    BNRoutePlanNode sNode = null;
	    BNRoutePlanNode eNode = null;
//		switch(coType) {
//			case GCJ02: {
//				sNode = new BNRoutePlanNode(116.30142, 40.05087, 
//			    		"百度大厦", null, coType);
//				eNode = new BNRoutePlanNode(116.39750, 39.90882, 
//			    		"北京天安门", null, coType);
//				break;
//			}
//			case WGS84: {
//				sNode = new BNRoutePlanNode(116.300821,40.050969,
//			    		"百度大厦", null, coType);
//				eNode = new BNRoutePlanNode(116.397491,39.908749, 
//			    		"北京天安门", null, coType);
//				break;
//			}
//			case BD09_MC: {
//				sNode = new BNRoutePlanNode(12947471,4846474,  
//						localDetail.street, null, coType);
//				eNode = new BNRoutePlanNode(12958160,4825947,  
//						mark.getTitle(), null, coType);
//				break;
//			}
//			default : ;
//		}
	    if(localDetail == null){
	    	localDetail = new AddressComponent();
	    	localDetail.street = "起点";
	    }
	    Log.d(TAG, "routeplanToNavi： "+localDetail.street + " : "+ local.getLatitude() + " ; " + local.getLongitude());
	    Log.d(TAG, "routeplanToNavi： "+mark.getTitle() + " : "+ mark.getPosition().latitude + " ; " + mark.getPosition().longitude);
	    sNode = new BNRoutePlanNode(local.getLongitude(),local.getLatitude(),  
	    		localDetail.street, null, coType);
		eNode = new BNRoutePlanNode(mark.getPosition().longitude,mark.getPosition().latitude,  
				mark.getTitle(), null, coType);
		if (sNode != null && eNode != null) {
	        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
	        list.add(sNode);
	        list.add(eNode);
	        BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
		}
	}
	
	
	public class DemoRoutePlanListener implements RoutePlanListener {
		
		private BNRoutePlanNode mBNRoutePlanNode = null;
		public DemoRoutePlanListener(BNRoutePlanNode node){
			mBNRoutePlanNode = node;
		}
		
		@Override
		public void onJumpToNavigator() {
			 Intent intent = new Intent(LocationActivity.this, BNDemoGuideActivity.class);
			 Bundle bundle = new Bundle();
			 bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			 intent.putExtras(bundle);
             startActivity(intent);
		}
		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			
		}
	}
	
	

	@Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    DrivingRouteOverlay overlay = null;
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
        	Log.d(TAG, "onGetDrivingRouteResult: ====================="+result.error);
            Toast.makeText(this, "抱歉，未找到线路结果"+result.error, Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            if(overlay != null)
            	overlay.removeFromMap();
            overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }


	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}


	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(LocationActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}

		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(LocationActivity.this, strInfo, Toast.LENGTH_LONG).show();
		
	}


	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Log.d(TAG, "onGetReverseGeoCodeResult: ====================="+result.error);
			Toast.makeText(LocationActivity.this, "抱歉，未能找到定位结果"+result.error, Toast.LENGTH_LONG)
					.show();
			
			NearbySearchInfo info = new NearbySearchInfo();
			info.ak = "QVE9iNrd5kfExC5PG0A2Er51";
			info.geoTableId = 125661;
			info.radius = 30000;
			info.location = local.getLongitude()+"," + local.getLatitude();//"116.403689,39.914957";
			CloudManager.getInstance().nearbySearch(info);
			return;
		}
		localDetail = result.getAddressDetail();
		LocalSearchInfo info = new LocalSearchInfo();
		info.ak = "QVE9iNrd5kfExC5PG0A2Er51";//"p63210Gut35HV9tuwjf6i6f3";
		info.geoTableId = 125661;//125049;
//		info.tags = "";
		info.q = "海关";
		info.region = localDetail.city;
		if(mName!=null && mRegion!=null){
			info.q = mName;
			info.region = mRegion;
		}
		CloudManager.getInstance().localSearch(info);
//		String city = result.getAddressDetail().city;
//		Toast.makeText(LocationActivity.this, result.getAddressDetail().province+","+result.getAddressDetail().city+","+result.getAddress(),
//				Toast.LENGTH_LONG).show();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.activity_fixed,
					R.anim.activity_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public void backProcess(View view){
		finish();
		overridePendingTransition(R.anim.activity_fixed,
				R.anim.activity_right_out);
	}

}
