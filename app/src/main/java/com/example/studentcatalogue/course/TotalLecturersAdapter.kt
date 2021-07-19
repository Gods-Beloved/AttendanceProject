package com.example.studentcatalogue.course

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


class TotalLecturersAdapter(val context: Context?) :
    RecyclerView.Adapter<TotalLecturersAdapter.TotalLecturersViewHolder>() {

    class TotalLecturersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val user: ParseUser = ParseUser.getCurrentUser()

        private val level = user.get("level").toString()

        private val className = "Level$level"


        val query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)
        val courseLecturer: TextView = itemView.findViewById(R.id.v_student_lecturer_name)
        val courseCode: TextView = itemView.findViewById(R.id.v_indexNumber_coursecode)
        val courseTotal: TextView = itemView.findViewById(R.id.v_total)
        val progressBar:ProgressBar= itemView.findViewById(R.id.progress_bar)



        val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.v_shimmer)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalLecturersViewHolder {

        val v = LayoutInflater.from(context).inflate(R.layout.total_item, parent, false)


        return TotalLecturersViewHolder(v)
    }


    override fun getItemCount(): Int {
        val user = ParseUser.getCurrentUser()

        val level = user.get("level").toString()

        val className = "Level$level"


        val size = ParseQuery.getQuery<ParseObject>(className)


        return size.count()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TotalLecturersViewHolder, position: Int) {

        holder.shimmerFrameLayout.showShimmer(true)
        holder.shimmerFrameLayout.startShimmer()

        holder.query.findInBackground { objects, e ->
            if (e == null) {


                val name = objects[position].getString("lecturer")
                val code = objects[position].getString("code")


                //  holder.courseCode.setPadding(0,0,0,0)
                holder.courseCode.setTextColor(Color.BLACK)
                holder.courseLecturer.setTextColor(Color.GRAY)

                holder.courseCode.text = code
                holder.courseLecturer.text = name

                holder.courseLecturer.setBackgroundResource(0)
                holder.courseCode.setBackgroundResource(0)

                val user = ParseUser.getCurrentUser()

                val indexNum = user.getString("indexNumber")


                val className2 = objects[position].getString("code").toString().replace("\\s".toRegex(), "")




                val query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className2)
                query.whereEqualTo("indexNumber", indexNum)

                query.getFirstInBackground { objects2, e2 ->
                    if (e2 == null) {
                        try {

                            val total = objects2.getInt("classAttended")

                            val className3="CustomData"


                            val codeNew = objects[position].getString("code").toString().replace("\\s".toRegex(), "")


                            val queryTotal= ParseQuery.getQuery<ParseObject>(className3)
                                .whereEqualTo("course",codeNew).first.getInt("totalLectures")

                            holder.progressBar.max=queryTotal
                            holder.progressBar.progress=total



                            if (queryTotal-total > 3){

                                holder.courseTotal.setTextColor(ContextCompat.getColor(context!!,R.color.red2))

                                holder.progressBar.progressTintList= ColorStateList.valueOf(Color.RED)

                                holder.courseTotal.text = "${total}/${queryTotal}"
                                holder.courseTotal.setBackgroundResource(0)

                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()

                            }else{


                                holder.courseTotal.setTextColor(Color.BLUE)
                                holder.courseTotal.setBackgroundResource(0)
                                holder.courseTotal.text = "${total}/$queryTotal"

                                holder.shimmerFrameLayout.stopShimmer()
                                holder.shimmerFrameLayout.hideShimmer()

                            }



                        } catch (e: IndexOutOfBoundsException) {


                            holder.courseTotal.setTextColor(Color.BLUE)
                            holder.courseTotal.setBackgroundResource(0)
                            holder.courseTotal.text =  "0"
                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()


                        }


                    } else {

                        if (e2.code == ParseException.CONNECTION_FAILED){
                            Toast.makeText(context,"No internet detected",Toast.LENGTH_LONG).show()
                        }else{
                            holder.courseTotal.setTextColor(Color.BLUE)
                            holder.courseTotal.setBackgroundResource(0)
                            holder.courseTotal.text = "0"
                            holder.shimmerFrameLayout.stopShimmer()
                            holder.shimmerFrameLayout.hideShimmer()
                        }



                    }

                }

            } else {

                if (e.code == ParseException.CONNECTION_FAILED){
                    Toast.makeText(context,"No internet detected",Toast.LENGTH_LONG).show()
                    holder.shimmerFrameLayout.stopShimmer()
                    holder.shimmerFrameLayout.hideShimmer()

                }else {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    holder.shimmerFrameLayout.stopShimmer()
                    holder.shimmerFrameLayout.hideShimmer()

                }
            }

            holder.shimmerFrameLayout.stopShimmer()
            holder.shimmerFrameLayout.hideShimmer()

        }


    }


}