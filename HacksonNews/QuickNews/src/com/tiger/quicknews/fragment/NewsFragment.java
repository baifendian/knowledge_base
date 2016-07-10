package com.tiger.quicknews.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.baifendian.mobile.BfdAgent;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.tiger.quicknews.R;
import com.tiger.quicknews.activity.*;
import com.tiger.quicknews.adapter.CardsAnimationAdapter;
import com.tiger.quicknews.adapter.NewAdapter;
import com.tiger.quicknews.bean.NewModle;
import com.tiger.quicknews.constant.Constant;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.Url;
import com.tiger.quicknews.http.json.NewListJson;
import com.tiger.quicknews.initview.InitView;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.slideingactivity.IntentUtils;
import com.tiger.quicknews.wedget.swiptlistview.SwipeListView;
import com.tiger.quicknews.wedget.viewimage.Animations.DescriptionAnimation;
import com.tiger.quicknews.wedget.viewimage.Animations.SliderLayout;
import com.tiger.quicknews.wedget.viewimage.SliderTypes.BaseSliderView;
import com.tiger.quicknews.wedget.viewimage.SliderTypes.BaseSliderView.OnSliderClickListener;
import com.tiger.quicknews.wedget.viewimage.SliderTypes.TextSliderView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@EFragment(R.layout.activity_main)
public class NewsFragment extends BaseFragment implements
		SwipeRefreshLayout.OnRefreshListener, OnSliderClickListener {
	protected SliderLayout mDemoSlider;
	@ViewById(R.id.swipe_container)
	protected SwipeRefreshLayout swipeLayout;
	@ViewById(R.id.listview)
	protected SwipeListView mListView;
	@ViewById(R.id.progressBar)
	protected ProgressBar mProgressBar;
	protected HashMap<String, String> url_maps;

	protected HashMap<String, NewModle> newHashMap;
	private Activity activity;
	@Bean
	protected NewAdapter newAdapter;
	protected List<NewModle> listsModles;
	private int index = 0;
	private boolean isRefresh = false;
	private String channelName;
	private String lastChannel = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		if (args != null) {
			channelName = args.getString("channel");
		}
		super.onCreate(savedInstanceState);
	}

	@AfterInject
	protected void init() {

		listsModles = new ArrayList<NewModle>();
		url_maps = new HashMap<String, String>();

		newHashMap = new HashMap<String, NewModle>();
	}

	@AfterViews
	protected void initView() {
		swipeLayout.setOnRefreshListener(this);
		InitView.instance().initSwipeRefreshLayout(swipeLayout);
		InitView.instance().initListView(mListView, getActivity());
		AnimationAdapter animationAdapter = new CardsAnimationAdapter(
				newAdapter);
		animationAdapter.setAbsListView(mListView);
		mListView.setAdapter(animationAdapter);
		loadData(getNewUrl(""));

		mListView.setOnBottomListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentPagte++;
				// index = index + 20;
//				loadData(getNewUrl(""));
				 loadLocalData();
				;
			}
		});
	}

	private void loadLocalData() {
		mListView.onBottomComplete();
		mProgressBar.setVisibility(View.GONE);
		// getMyActivity().showShortToast(getString(R.string.not_network));
		if (channelName != null && !channelName.equals(lastChannel)) {
			lastChannel = channelName;
			String result = getMyActivity().getCacheStr(
					channelName + currentPagte);
			if (!StringUtils.isEmpty(result)) {
				getResult(result);
			}
		}
	}

	private void loadData(String url) {
		if (getMyActivity().hasNetWork()) {
			loadNewList(url);
		}
	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				currentPagte = 1;
				isRefresh = true;
				loadData(getNewUrl("0"));
				url_maps.clear();
				// mDemoSlider.removeAllSliders();
			}
		}, 2000);
	}

	@ItemClick(R.id.listview)
	protected void onItemClick(int position) {
		NewModle newModle = listsModles.get(position);
		enterDetailActivity(newModle);
	}

	public void enterDetailActivity(NewModle newModle) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("newModle", newModle);
		Class<?> class1;
		if (newModle.getImagesModle() != null
				&& newModle.getImagesModle().getImgList().size() > 1) {
			class1 = ImageDetailActivity_.class;
		} else {
			class1 = DetailsActivity_.class;
		}
		((BaseActivity) getActivity()).openActivity(class1, bundle, 0);
	}

	@Background
	void loadNewList(String url) {
		// String result;
		try {
			// result = HttpUtil.getByHttpClient(getActivity(), url);
			getRecData(url);
			/*
			 * if (!StringUtils.isEmpty(result)) { getResult(result); } else {
			 * swipeLayout.setRefreshing(false); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取推荐
	private String result = null;

	public String getRecData(String url) {
		// String result = null;
		Map<String, String> params = new HashMap<String, String>();
		params.put(
				"fmt",
				"{\"iid\":\"$iid\",\"url\":\"$url\"," +
				"\"img\":\"$smallPic\"," +
				"\"ptime\":\"$posttime\"," +
				"\"title\":\"$title\"," +
				"\"digest\":\"$abs\"," +
				"\"topic\":\"$topic\"}");

		params.put("num", "20");
		if(!"推荐".equals(channelName)){
			params.put("topic", channelName);
		}
		// params.put("iid","100020000");
		BfdAgent.recommend(activity,
				"rec_C6613205_93B6_61A6_9FEC_180B70F91B94", params,
				new BfdAgent.Runnable() {

					@Override
					public void run(String arg0, JSONArray arg1) {
						try {
							//System.out.println(arg1.toString());
							if (arg1 != null && arg1.length() > 0) {

								if (arg1.length() >= 10) {
									JSONArray js = new JSONArray();
									for (int i = 10; i < arg1.length(); i++) {
										js.put(arg1.remove(i));
									}
									getMyActivity().setCacheStr(
											channelName + 2, js.toString());
								}
								result = arg1.toString();
								if (!StringUtils.isEmpty(result)) {
									getResult(result);
								} else {
									swipeLayout.setRefreshing(false);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		return result;
	}

	@UiThread
	public void getResult(String result) {
		getMyActivity().setCacheStr(channelName + currentPagte, result);
		if (isRefresh) {
			isRefresh = false;
			newAdapter.clear();
			listsModles.clear();
		}
		mProgressBar.setVisibility(View.GONE);
		swipeLayout.setRefreshing(false);
		JSONArray results = null;
		if (result != null) {
			try {
				results = new JSONArray(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 添加 数据
		List<NewModle> list = new ArrayList<NewModle>();
		if (results != null && results.length() > 0) {
			for (int i = 0; i < results.length(); i++) {
				JSONObject js;
				try {
					js = results.getJSONObject(i);
					list.add(getNewModle(js));
				} catch (JSONException e) {
				}
			}
		}
		newAdapter.appendList(list);
		listsModles.addAll(list);
		mListView.onBottomComplete();
	}

	private NewModle getNewModle(JSONObject jsonObject) {
		// NewListJson nlj = NewListJson.instance(getActivity());
		String docid = "";
		String title = "";
		String digest = "";
		String imgsrc = "";
		String source = "";
		String ptime = "";
		String tag = "";
		try {
			docid = jsonObject.getString("iid");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			title = jsonObject.getString("title");
		} catch (JSONException e) {
		}
		// digest = ;
		try {
			imgsrc = jsonObject.getString("img");
		} catch (JSONException e) {
		}
		try {
			source = jsonObject.getString("source");
		} catch (JSONException e) {
		}
		try {
			ptime = jsonObject.getString("ptime");
		} catch (JSONException e) {
		}
		try {
			tag = jsonObject.getString("topic");
		} catch (JSONException e) {
		}
		try {
			digest = jsonObject.getString("digest");
		} catch (JSONException e) {
		}
		NewModle newModle = new NewModle();

		newModle.setDigest(digest);
		newModle.setDocid(docid);
		//imgsrc = "http://static.codeceo.com/images/2015/05/ac7ede9a6b711a6dea54881b5ef55e49-140x98.png";
		if(!"null".equals(imgsrc)&&!"".equals(imgsrc) && imgsrc != null){
			newModle.setItemType("IMG");
			newModle.setImgsrc(imgsrc);
		}else{
			newModle.setItemType("IMG");
			String img = getImgStr();
			newModle.setImgsrc(img);
//			newModle.setItemType("ITEM");
		}
		newModle.setTitle(title);
		newModle.setPtime(ptime);
		newModle.setSource(source);
		newModle.setTag(tag);
		return newModle;
	}
	private String getImgStr(){
		
		Random random = new Random();
		int i = random.nextInt(4);
		if("JS".equals(channelName)){
			return Constant.JSImgSrcArray[i];
		}else if("IOS".equals(channelName)){
			return Constant.IOSImgSrcArray[i];
		}else if("Linux".equals(channelName)){
			return Constant.LINUXImgSrcArray[i];
		}else if("python".equals(channelName)){
			return Constant.PYTHONImgSrcArray[i];
		}else if("笑话".equals(channelName) || "美女".equals(channelName)){
			return Constant.ImgSrcArray[i];
		}
		return Constant.TYImgSrcArray[i];
	}
	@Override
	public void onSliderClick(BaseSliderView slider) {
		NewModle newModle = newHashMap.get(slider.getUrl());
		enterDetailActivity(newModle);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
