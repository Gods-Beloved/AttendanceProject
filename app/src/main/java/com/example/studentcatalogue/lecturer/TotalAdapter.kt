package com.example.studentcatalogue.lecturer

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*

class TotalAdapter(val context: Context?) : RecyclerView.Adapter<TotalAdapter.TotalViewHolder>() {

    val user: ParseUser = ParseUser.getCurrentUser()
    var className = user.getString("code").toString().replace("\\s".toRegex(), "")


    private val level = user.get("level").toString()


    var query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)

   var queryStudents: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>("Student").whereEqualTo("level", level)



    class TotalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {





        val courseIndex: TextView = itemView.findViewById(R.id.v_indexNumber_coursecode)
        val courseStudent: TextView = itemView.findViewById(R.id.v_student_lecturer_name)
        val total: TextView = itemView.findViewById(R.id.v_total)
        val progressBar:ProgressBar = itemView.findViewById(R.id.progress_bar)


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




     queryStudents.findInBackground { objects, e ->

            if (e == null) {

                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()

                val index =objects[position].getString("indexNumber")
                val uName = objects[position].getString("userName")


                //  holder.courseCode.setPadding(0,0,0,0)
                holder.courseIndex.setTextColor(Color.BLACK)
                holder.courseStudent.setTextColor(Color.GRAY)
                holder.courseStudent.setBackgroundResource(0)
                holder.courseIndex.setBackgroundResource(0)


                holder.courseIndex.text = index
                holder.courseStudent.text = uName




              query.whereEqualTo("indexNumber",index)

                query.getFirstInBackground {
                        objects2, e2 ->
                    if (e2 == null) {
                        try {

                            val total = objects2.getInt("classAttended")

                            val className3="CustomData"

                            val user: ParseUser = ParseUser.getCurrentUser()
                            val className = user.getString("code").toString().replace("\\s".toRegex(), "")


                            val queryTotal= ParseQuery.getQuery<ParseObject>(className3)
                                .whereEqualTo("course",className).first.getInt("totalLectures")


                            holder.progressBar.max=queryTotal
                            holder.progressBar.progress=total



                            if (queryTotal-total >= 3){

                                holder.total.setTextColor(ContextCompat.getColor(context!!,R.color.red2))
                                holder.courseIndex.setTextColor(ContextCompat.getColor(context,R.color.red2))
                                holder.courseStudent.setTextColor(ContextCompat.getColor(context,R.color.red2))

                                holder.progressBar.progressTintList= ColorStateList.valueOf(Color.RED)

                                holder.total.text = "${total}/${queryTotal}"
                                holder.total.setBackgroundResource(0)

                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()

                            }else{


                                holder.total.setTextColor(Color.BLUE)
                                holder.total.text = "${total}/${queryTotal}"
                                holder.total.setBackgroundResource(0)

                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()

                            }



                        }catch (e :IndexOutOfBoundsException){


                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()
                        }

                    } else {
                        if (e2.code == ParseException.CONNECTION_FAILED){
                            Toast.makeText(context,"No internet detected", Toast.LENGTH_LONG).show()
                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()

                        }else if(e2.code == ParseException.OBJECT_NOT_FOUND){
                            holder.total.text = "0"
                            holder.total.setTextColor(Color.BLUE)
                            holder.total.setBackgroundResource(0)
                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()
                        }else{


                            val className3="CustomData"

                            val user: ParseUser = ParseUser.getCurrentUser()
                            val className = user.getString("code").toString().replace("\\s".toRegex(), "")


                            val queryTotal= ParseQuery.getQuery<ParseObject>(className3)
                                .whereEqualTo("course",className).first.getInt("totalLectures")

                            if (queryTotal > 3){
                                holder.total.text = "0/$queryTotal"
                                holder.total.setTextColor(ContextCompat.getColor(context!!,R.color.red2))
                                holder.courseStudent.setTextColor(ContextCompat.getColor(context,R.color.red2))
                                holder.courseIndex.setTextColor(ContextCompat.getColor(context,R.color.red2))
                                holder.total.setBackgroundResource(0)
                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()
                            }else{
                                holder.total.text = "0/$queryTotal"
                                holder.total.setTextColor(Color.BLUE)
                                holder.total.setBackgroundResource(0)
                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()
                            }


                        }

                    }

                }

            } else {

                if (e.code == ParseException.CONNECTION_FAILED){
                    Toast.makeText(context,"No internet detected",Toast.LENGTH_LONG).show()
                    holder.shimmerFrameLayout.stopShimmer()
                    holder.shimmerFrameLayout.hideShimmer()

                }

                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()


            }
        }




    }
//
//    override fun getFilter(): Filter {
//        return object:Filter(){
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                var filteredList = arrayListOf<ParseObject>()
//                val user: ParseUser = ParseUser.getCurrentUser()
//                val level = user.get("level").toString()
//
//                if(constraint == null || constraint.length == 0){
//
//                    filteredList.addAll(queryStudents.find())
//
//                }else {
//                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
//
//                    for (i in queryStudents.whereContains("userName",filterPattern).find()){
//
//                           filteredList.add(i)
//
//
//                   }
//
//                }
//                val filteredResults=FilterResults()
//                filteredResults.values=filteredList
//                return filteredResults
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                queryStudents.find().removeAll(queryStudents.find())
//                queryStudents.find().addAll((results!!.values) as List<ParseObject>)
//                notifyDataSetChanged()
//            }
//    }
//
//
//
//    }


}
