package com.hr.permissionsmanager.home.views

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import butterknife.BindView
import com.hr.permissionsmanager.R
import com.hr.permissionsmanager.common.PmApplication
import com.hr.permissionsmanager.common.activities.BaseActivity
import com.hr.permissionsmanager.common.widgets.SimpleDividerItemDecoration
import com.hr.permissionsmanager.home.adapters.AppListAdapter
import com.hr.permissionsmanager.home.presenter.PermissionListPresenter
import com.hr.permissionsmanager.home.presenter.PermissionListPresenterImpl
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.lang.ref.WeakReference


class PermissionListActivity : BaseActivity(), PermissionListView, View.OnClickListener {


    @BindView(R.id.list) @JvmField var list: RecyclerView? = null
    @BindView(R.id.toolbar) @JvmField var toolbar: Toolbar? = null
    @BindView(R.id.progress) @JvmField var progress: ProgressBar? = null

    @JvmField var presenter: PermissionListPresenter? = null
    @JvmField var adapter: AppListAdapter? = null


    override val layout: Int
        get() = R.layout.activity_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        toolbar!!.elevation = resources.getDimension(R.dimen.spacing_small)
        list!!.addItemDecoration(SimpleDividerItemDecoration(this))
        list!!.layoutManager = LinearLayoutManager(this)
        adapter = AppListAdapter(ArrayList(), packageManager, this)
        list!!.adapter = adapter
        list!!.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.permission_category_title, intent.getStringExtra("permission_group_title"))
        presenter = PermissionListPresenterImpl(WeakReference(this), PmApplication.instance, intent.getStringExtra("permission_group"))
        presenter!!.fetchApps()
    }

    override fun toggleProgress(isVisible: Boolean) {
        progress!!.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun toggleError(isVisible: Boolean) {

    }

    override fun addAppToView(app: PackageInfo) {
        adapter!!.addApp(app)
    }

    override fun onClick(view: View?) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", (view!!.tag as PackageInfo).packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onDestroy() {
        presenter!!.onDestroyed()
        super.onDestroy()

    }

}