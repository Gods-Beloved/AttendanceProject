package com.example.studentcatalogue.lecturer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser

class TotalAdapter(val context: Context?) : RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {

//    private lateinit var mListener: OnItemClickListener
//
//    interface OnItemClickListener {
//        fun onItemClick(position: Int, intent: Intent)
//        //  fun onItemClick(position: Int)
//
//    }
//
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        mListener = listener
//    }


    class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val user = ParseUser.getCurrentUser()
        var className = user.getString("code").toString().replace("\\s".toRegex(), "")


        private val level = user.get("level").toString()


        val query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)
        val querystudents: ParseQuery<ParseObject> =
            ParseQuery.getQuery<ParseObject>("Student").whereEqualTo("level", level)
        val courseIndex: TextView = itemView.findViewById(R.id.v_indexNumber_coursecode)
        val courseStudent: TextView = itemView.findViewById(R.id.v_student_lecturer_name)
        val total: TextView = itemView.findViewById(R.id.v_total)


        val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.v_shimmer)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {

        val v = LayoutInflater.from(context).inflate(R.layout.total_item, parent, false)


        return TotalViewHolder(v)
    }


    override fun getItemCount(): Int {
        val user = ParseUser.getCurrentUser()
        val level = user.get("level").toString()
        val querystudents: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Student").whereEqualTo("level", level)

        return querystudents.count()
        //size.count()
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        holder.shimmerFrameLayout.startShimmer()
        holder.shimmerFrameLayout.showShimmer(true)



        holder.querystudents.findInBackground { objects, e ->

            if (e == null) {

                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()

                val index = objects[position].getString("indexNumber")
                val uName = objects[position].getString("userName")




                //  holder.courseCode.setPadding(0,0,0,0)
                holder.courseIndex.setTextColor(Color.BLACK)
                holder.courseStudent.setTextColor(Color.GRAY)
                holder.courseStudent.setBackgroundResource(0)
                holder.courseIndex.setBackgroundResource(0)


                holder.courseIndex.text = index
                holder.courseStudent.text = uName


                holder.query.findInBackground { objects2, e2 ->
                    if (e2 == null) {
                        try {
                            val index2 = objects2[position].getString("indexNumber")
                            val uName2 = objects2[position].getString("name")
                            val total = objects2[position].getInt("classAttended")
                            holder.courseIndex.setTextColor(Color.BLACK)
                            holder.courseIndex.setBackgroundResource(0)
                            holder.courseIndex.text = index2

                            holder.courseStudent.setTextColor(Color.GRAY)
                            holder.courseStudent.setBackgroundResource(0)
                            holder.courseStudent.text = uName2


                            holder.total.setTextColor(Color.BLUE)
                            holder.total.setBackgroundResource(0)
                            holder.total.text = total.toString()

                        }catch (e :IndexOutOfBoundsException){
                            holder.total.setTextColor(Color.BLUE)
                            holder.total.setBackgroundResource(0)
                            holder.total.text = "0"
                        }



                    } else {
                        Toast.makeText(context, e2.message, Toast.LENGTH_LONG).show()
                    }

                }

            } else {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()


            }
        }


//        holder.itemView.setOnClickListener {
//            val position2 = holder.adapterPosition
//
//            val value = holder.courseIndex.text.trim().toString()
//
//            val intent = Intent(context, Scanner::class.java)
//            intent.putExtra("courseCode", value)
//
//
//
//           mListener.onItemClick(position2, intent)
//        }

//        holder.itemView.setOnLongClickListener {
//            val pos=holder.adapterPosition
//            mListener.onItemClick(pos)
//            return@setOnLongClickListener true
//
//        }

    }


}