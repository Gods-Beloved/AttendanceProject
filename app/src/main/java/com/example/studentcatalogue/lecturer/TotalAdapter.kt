package com.example.studentcatalogue.lecturer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser

class TotalAdapter(val context: Context?) : RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {



    class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val user: ParseUser = ParseUser.getCurrentUser()
        var className = user.getString("code").toString().replace("\\s".toRegex(), "")


        private val level = user.get("level").toString()


        val query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)
        val queryStudents: ParseQuery<ParseObject> =
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
        val queryStudent: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Student").whereEqualTo("level", level)

        return queryStudent.count()
        //size.count()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {



        holder.shimmerFrameLayout.startShimmer()
        holder.shimmerFrameLayout.showShimmer(true)




        holder.queryStudents.findInBackground { objects, e ->

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







                holder.query.whereEqualTo("indexNumber",index)

                holder.query.getFirstInBackground {
                        objects2, e2 ->
                    if (e2 == null) {
                        try {

                            val total = objects2.getInt("classAttended").toString()

                            val className3="CustomData"

                            val user: ParseUser = ParseUser.getCurrentUser()
                            val className = user.getString("code").toString().replace("\\s".toRegex(), "")


                            val queryTotal= ParseQuery.getQuery<ParseObject>(className3)
                                .whereEqualTo("course",className).first.getInt("totalLectures").toString()

                            holder.total.setTextColor(Color.BLUE)
                            holder.total.text = "${total}/$queryTotal"
                            holder.total.setBackgroundResource(0)
                        }catch (e :IndexOutOfBoundsException){


                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()
                        }

                    } else {
                        holder.total.text = "0"
                        holder.total.setTextColor(Color.BLUE)
                        holder.total.setBackgroundResource(0)
                        holder.shimmerFrameLayout.stopShimmer()
                        holder.shimmerFrameLayout.hideShimmer()

                    }

                }

            } else {

                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()


            }
        }




    }



}