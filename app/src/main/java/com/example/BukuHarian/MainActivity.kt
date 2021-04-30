package com.example.BukuHarian

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_crud.DatabaseHandler
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_detail.*
import kotlinx.android.synthetic.main.dialog_update.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        simpandata.setOnClickListener {
            setupListOfDataIntoRecyclerView()
            addRecord()
            closeKeyboard()

            //Untuk menyembunyikan keyboard ketika pertama kali dipilih
            txt_date.inputType = InputType.TYPE_NULL
            txt_time.inputType = InputType.TYPE_NULL
        }

        //Aksi yang akan dijalankan ketika perama kali dipilih
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMont: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DATE, dayOfMont)
                updateDateInView()
            }
        }
        //Aksi yang akan dijalankan ketika timepicker di pilih
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                updateTimeInView()
            }
        }
        //when you click on the edit text, show DatePickerDialog that is set with OnDataListener
        txt_date.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                closeKeyboard()
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    //set DatePickerDialog to point to today's date when it load up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })
        txt_time.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                closeKeyboard()
                TimePickerDialog(
                    this@MainActivity,
                    timeSetListener,
                    //set DatePickerDialog to point to today's date when it load up
                    cal.get(Calendar.HOUR),
                    cal.get(Calendar.MINUTE), true
                ).show()
            }
        })
    }

    //untuk close keyboard
    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //untul updatedate
    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txt_date.setText(sdf.format(cal.time))
    }

    //untuk updatetime
    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        txt_time.setText(sdf.format(cal.time))
    }

    //untuk membuat data
    private fun addRecord() {
        val date = txt_date.text.toString()
        val time = txt_time.text.toString()
        val dateTime = "${date}(${time}"
        val descritions = txt_desc.text.toString()
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        if (!date.isEmpty() && !time.isEmpty() && !descritions.isEmpty()) {
            val status =
                databaseHandler.addEmployee(EmpModel(0, dateTime, descritions))
            if (status > -1) {
                Toast.makeText(applicationContext, "Berhasil Menambahkan data", Toast.LENGTH_SHORT).show()
                txt_date.text.clear()
                txt_time.text.clear()
                txt_desc.text.clear()
                closeKeyboard()
            }
        } else {
            Toast.makeText(applicationContext, "Data tidak boleh kosong!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // method untuk mendapatkan jumlah record
    private fun getItemList(): ArrayList<EmpModel> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<EmpModel> = databaseHandler.viewEmployee()

        return empList
    }

    //method to show data to recycler
    private fun setupListOfDataIntoRecyclerView() {
        if (getItemList().size > 0) {
            rv_item.visibility = View.VISIBLE
            norecord.visibility = View.GONE

            rv_item.layoutManager = LinearLayoutManager(this)
            rv_item.adapter = ItemAdapter(this, getItemList())
        } else {
            rv_item.visibility = View.GONE
            norecord.visibility = View.VISIBLE
        }
    }

    // method untuk menampilkan dialog konfirmasi delete
    fun deleteRecordAlertDialog(empModel: EmpModel) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Hapus?")
        builder.setMessage("Hapus Data Terpilih?")
        builder.setIcon(android.R.drawable.ic_delete)

        // menampilkan tombol yes
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(EmpModel(empModel.id, "", ""))

            if (status > -1) {
                Toast.makeText(this, "Berhasil menghapus", Toast.LENGTH_SHORT).show()
                setupListOfDataIntoRecyclerView()
            }

            dialog.dismiss()
        }
        // menampilkan tombol no
        builder.setNegativeButton("No") { dialog: DialogInterface, which ->
            //Toast.makeText(this, "No", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        // menampilkan user menekan tombol yes or no
        alertDialog.setCancelable(false)
        // menampilkan kotak dialog
        alertDialog.show()
    }

    //metode untuk menampilkan dialog pembaruan khusus
//    fun updateRecordDialog(empModel1: EmpModel) {
//        val updateDialog = Dialog(this, R.style.Theme_Dialog)
//
//        updateDialog.setCancelable(false)
//        updateDialog.setContentView(R.layout.dialog_update)
//
//        //Memecah datetime berdasarkan karakter
//        val datetime = (empModel1.time).split("(")
//        val date = datetime[0]
//        val time = datetime[1]
//
//        //Memecah date berdasarkan karakter
//        val dateList = date.split("/")
//        val year = dateList[2].toInt()
//        val month = dateList[1].toInt() - 1
//        val day = dateList[0].toInt()
//
//        time = time.dropLast(1)
//        val timeList = time.split(":")
//        //Memecah time berdasarkan karakter
//        val hour = timeList[0].toInt()
//        val minute = timeList[1].toInt()
//
//        updateDialog.etUpdateDate.setText(date)
//        updateDialog.etUpdateTime.setText(time)
//        updateDialog.etUpdateDesc.setText(empModel1.desc)
//
//        updateDialog.etUpdateDate.inputType = InputType.TYPE_NULL
//        updateDialog.etUpdateTime.inputType = InputType.TYPE_NULL
//
//        updateDialog.tvUpdated.setOnClickListener{
//            val time = updateDialog.etUpdateDate.text.toString()
//            val date = updateDialog.etUpdateTime.text.toString()
//
//            // Menghubungkan ke database
//        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
//
//        if (!date.isEmpty() && !time.isEmpty()) {
//            val status = databaseHandler.updateEmployee(EmpModel(empModel1.id, date, time))
//            if (status > -1) {
//                Toast.makeText(this, "Berhasil Ubah data", Toast.LENGTH_SHORT).show()
//                setupListOfDataIntoRecyclerView()
//                updateDialog.dismiss()
//                closeKeyboard()
//            }
//        } else {
//            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
//        }
//
//        Toast.makeText(this, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show()
//    }
//    updateDialog.tvCancel.setOnClickListener
//    {
//        updateDialog.dismiss()
//    }
//    updateDialog.show()
//    closeKeyboard()
//
//}
    fun showDetail(empModel: EmpModel){
        val showdetail = Dialog(this,R.style.Theme_Dialog)

        showdetail.setCancelable(false)
        showdetail.setContentView(R.layout.dialog_detail)

        showdetail.etUpdateTanggal1.setText(empModel.time)
        showdetail.etUpdateDesripsi1.setText(empModel.desc)

        showdetail.tvSelesai.setOnClickListener{
            showdetail.dismiss()
        }
        showdetail.show()
    }
}