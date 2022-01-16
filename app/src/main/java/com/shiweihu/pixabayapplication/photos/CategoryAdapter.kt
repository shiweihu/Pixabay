package com.shiweihu.pixabayapplication.photos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.CategoryItemLayoutBinding

class CategoryAdapter(val context: Context) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categorys by lazy {
        context.resources.getStringArray(R.array.category_text)
    }

    val checkedList = ArrayList<String>()




    class CategoryViewHolder(val binding:CategoryItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemLayoutBinding.inflate( LayoutInflater.from(parent.context),parent,false )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categorys[position]
        holder.binding.categoryText = category
        holder.binding.checkbox.isChecked = checkedList.contains(category)
        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checkedList.add(category)
            }else{
                checkedList.remove(category)
            }
        }

    }

    override fun getItemCount(): Int {
        return categorys.size
    }
}