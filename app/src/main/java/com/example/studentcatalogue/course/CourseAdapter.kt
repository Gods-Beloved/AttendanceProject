package com.example.studentcatalogue.course

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcatalogue.R
import com.example.studentcatalogue.Scanner
import com.facebook.shimmer.ShimmerFrameLayout
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser


class CourseAdapter(val context: Context?) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, intent: Intent)
        //  fun onItemClick(position: Int)

    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private val user: ParseUser = ParseUser.getCurrentUser()

        private val level = user.get("level").toString()

        private val className = "Level$level"


        val query: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)
        val courseLecturer: TextView = itemView.findViewById(R.id.v_couse_lecturer)
        val courseCode: TextView = itemView.findViewById(R.id.v_coursecode)


        val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.v_shimmer)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

        val v = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)


        return CourseViewHolder(v)
    }


    override fun getItemCount(): Int {
        val user = ParseUser.getCurrentUser()

        val level = user.get("level").toString()

        val className = "Level$level"


        val size = ParseQuery.getQuery<ParseObject>(className)


        return size.count()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.shimmerFrameLayout.startShimmer()
        holder.shimmerFrameLayout.showShimmer(true)

        holder.query.findInBackground { objects, e ->
            if (e == null) {


                val name = objects[position].getString("lecturer")
                val code = objects[position].getString("code")


                //  holder.courseCode.setPadding(0,0,0,0)
                holder.courseCode.setTextColor(Color.BLACK)
                holder.courseLecturer.setTextColor(Color.GRAY)
                holder.courseLecturer.setBackgroundResource(0)
                holder.courseCode.setBackgroundResource(0)


                holder.courseCode.text = code
                holder.courseLecturer.text = name

                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()

            } else {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                holder.shimmerFrameLayout.stopShimmer()
                holder.shimmerFrameLayout.hideShimmer()


            }

        }





        holder.itemView.setOnClickListener {

            val className="CustomData"
            val queryCode: ParseQuery<ParseObject> = ParseQuery.getQuery<ParseObject>(className)



            queryCode.getInBackground("3Iw08trymI") { `object`, e ->
                if (e == null) {

                   val current= `object`.getString("latestCode").toString()
                    val position2 = holder.adapterPosition

                    val value = holder.courseCode.text.trim().toString()


                    val intent = Intent(context, Scanner::class.java)
                    intent.putExtra("courseCode", value)
                    intent.putExtra("codeGen",current)


                    mListener.onItemClick(position2, intent)

                } else {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }




        }



    }


}