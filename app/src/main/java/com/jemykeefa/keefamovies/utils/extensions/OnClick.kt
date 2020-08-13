package com.jemykeefa.keefamovies.utils.extensions

import androidx.recyclerview.widget.RecyclerView

fun <T : RecyclerView.ViewHolder> T.listen(event:(position: Int, type : Int) -> Unit) : T{
    itemView.setOnClickListener{
        event.invoke(adapterPosition, itemViewType)
    }
    return this
}