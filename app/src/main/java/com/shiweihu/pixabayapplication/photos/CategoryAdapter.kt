package com.shiweihu.pixabayapplication.photos

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shiweihu.pixabayapplication.R
import com.shiweihu.pixabayapplication.databinding.CategoryItemLayoutBinding

class CategoryAdapter(val context: Context,val callBack:(category:String)->Unit) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val categorys by lazy {
        context.resources.getStringArray(R.array.category_text)
    }

    var checkedItem = ""
    var checkedPosition = -1




    class CategoryViewHolder(val binding:CategoryItemLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemLayoutBinding.inflate( LayoutInflater.from(parent.context),parent,false )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categorys[position]
        holder.binding.categoryText = category
        holder.binding.checkbox.setOnCheckedChangeListener(null)
        holder.binding.checkbox.isChecked = category == checkedItem
        holder.binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                checkedItem = category
                this.notifyItemChanged(checkedPosition)
                checkedPosition = position
            }else{
                checkedItem = ""
            }
            callBack(checkedItem)
        }



    }

    override fun getItemCount(): Int {
        return categorys.size
    }
}