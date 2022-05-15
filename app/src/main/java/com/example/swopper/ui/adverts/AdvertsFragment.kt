package com.example.swopper.ui.adverts

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.swopper.R
import com.example.swopper.database.*
import com.example.swopper.models.AdvertModel
import com.example.swopper.utils.*
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_adverts.*
import kotlin.math.ceil

class AdvertsFragment : Fragment(R.layout.fragment_adverts) {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AdvertsAdapter
    private lateinit var mAdvertsListener: AppValueEventListener
    private var mAdverts = mutableListOf<AdvertModel>()

    private var mQueryFilter: String = ""
    private var mTypeFilter: String = "Не важно"
    private var mCategoryFilter: String = "Не важно"
    private var mLocationFilter: String = "Не важно"

    override fun onStart() {
        super.onStart()

        APP_ACTIVITY.mBottomNavigationView.visibility = View.VISIBLE
        APP_ACTIVITY.supportActionBar?.show()
        val mChatToolbar = APP_ACTIVITY.mActionBarToolbar.chat_toolbar
        mChatToolbar.visibility = View.GONE
        APP_ACTIVITY.title = "Объявления"

        setHasOptionsMenu(true)

        initRecyclerView()

        initSearchBar()
    }

    private fun initRecyclerView() {
        mRecyclerView = adverts_recycler_view
        mAdapter = AdvertsAdapter()

        mAdvertsListener = AppValueEventListener { dataSnapshot ->
            val adverts =
                dataSnapshot.children.map { it.getValue(AdvertModel::class.java) ?: AdvertModel() }
            mAdverts.clear()
            adverts.forEach { advert ->
                if (advert.owner != CURRENT_UID && advert.status == AdvertStatus.ACTIVE.status) {
                    mAdverts.add(advert)
                } }
            mAdverts.sortByDescending { advert -> advert.posted.toString().toLong() }
            mAdapter.setList(mAdverts)
        }

        REF_DATABASE_ROOT.child(NODE_ADVERTS).addValueEventListener(mAdvertsListener)

        mRecyclerView.adapter = mAdapter
    }

    private fun initSearchBar() {
        search_bar.isSuggestionsEnabled = false
        search_bar.hideSuggestionsList()
        search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    mQueryFilter = ""
                    filter()
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                mQueryFilter = text.toString()
                hideKeyboard()
                filter()
            }

            override fun onButtonClicked(buttonCode: Int) {
            }
        })
    }

    private fun filter() {
        REF_DATABASE_ROOT.child(NODE_ADVERTS).removeEventListener(mAdvertsListener)

        mAdvertsListener = AppValueEventListener { dataSnapshot ->
            val adverts =
                dataSnapshot.children.map { it.getValue(AdvertModel::class.java) ?: AdvertModel() }
            mAdverts.clear()
            adverts.forEach { advert ->
                if (advert.owner != CURRENT_UID
                    && advert.status == AdvertStatus.ACTIVE.status
                    && (mQueryFilter == "" || search(advert.name, mQueryFilter))
                    && (mCategoryFilter == "Не важно" || advert.category == mCategoryFilter)
                    && (mTypeFilter == "Не важно" || advert.type == mTypeFilter)
                    && (mLocationFilter == "Не важно" || advert.location == mLocationFilter)
                ) {
                    mAdverts.add(advert)
                }
            }
            mAdverts.sortByDescending { advert -> advert.posted.toString().toLong() }
            mAdapter.setList(mAdverts)
        }

        REF_DATABASE_ROOT.child(NODE_ADVERTS).addValueEventListener(mAdvertsListener)
    }

    private fun search(name: String, query: String): Boolean {
        val pattern = query.toLowerCase()
        for (input in name.toLowerCase().split(" ")) {
            if (bitap(input, pattern, ceil(0.2 * pattern.length).toInt())) {
                return true
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.filters_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.filters).isVisible = true
        menu.findItem(R.id.filters).isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filters -> {
                chooseFilters()
            }
        }
        return true
    }

    private fun chooseFilters() {
        val builder = AlertDialog.Builder(APP_ACTIVITY, R.style.filterDialog)
        builder.setTitle("Фильтры")

        val mLayout: View = layoutInflater.inflate(R.layout.fragment_filter, null)

        builder.setView(mLayout)

        val mTypeRadioGroup: RadioGroup = mLayout.findViewById(R.id.filter_type)
        val mCategorySpinner: Spinner = mLayout.findViewById(R.id.filter_category)
        val mLocationSpinner: Spinner = mLayout.findViewById(R.id.filter_location)

        val categoriesAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.categories_with_default,
            R.layout.simple_spinner_item
        )
        categoriesAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        mCategorySpinner.adapter = categoriesAdapter

        val locationAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            APP_ACTIVITY,
            R.array.locations_with_default,
            R.layout.simple_spinner_item
        )
        locationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        mLocationSpinner.adapter = locationAdapter

        mTypeRadioGroup.check(
            when (mTypeFilter) {
                "Отдам даром" -> R.id.filter_type_give
                "Приму в дар" -> R.id.filter_type_accept
                "Обменяю" -> R.id.filter_type_change
                else -> R.id.filter_type_default
            }
        )

        val categories: MutableList<String> = mutableListOf()
        categories.addAll(APP_ACTIVITY.resources.getStringArray(R.array.categories_with_default))

        mCategorySpinner.setSelection(categories.indexOf(mCategoryFilter))

        val locations: MutableList<String> = mutableListOf()
        locations.addAll(APP_ACTIVITY.resources.getStringArray(R.array.locations_with_default))

        mLocationSpinner.setSelection(locations.indexOf(mLocationFilter))

        val alert = builder.setPositiveButton("ОК") { dialog, _ ->
            run {
                mTypeFilter = when (mTypeRadioGroup.checkedRadioButtonId) {
                    R.id.filter_type_give -> "Отдам даром"
                    R.id.filter_type_accept -> "Приму в дар"
                    R.id.filter_type_change -> "Обменяю"
                    else -> "Не важно"
                }
                mCategoryFilter = mCategorySpinner.selectedItem.toString()
                mLocationFilter = mLocationSpinner.selectedItem.toString()
                filter()
                dialog.cancel()
            }
        }
            .setNeutralButton("Отмена") { dialog, _ ->
                run {
                    dialog.cancel()
                }
            }
            .setNegativeButton("Сброс") { dialog, _ ->
                run {
                    mTypeFilter = "Не важно"
                    mCategoryFilter = "Не важно"
                    mLocationFilter = "Не важно"
                    filter()
                    dialog.cancel()
                }
            }.create()

        alert.show()

        alert.window?.setLayout(1050, 1550)

        alert.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorSecondary))
        alert.getButton(DialogInterface.BUTTON_NEUTRAL)
            .setTextColor(resources.getColor(R.color.colorMainText))
        alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.colorCancelButton))
    }
}