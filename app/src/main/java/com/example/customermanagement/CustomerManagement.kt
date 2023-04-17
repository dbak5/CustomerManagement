package com.example.customermanagement

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.lang.Integer.parseInt

/*
Name: DaHye Baker
ID: 30063368
Assessment 2: Activity 2
*/

class CustomerManagement : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "CutPasteId") //CHECK REMOVE THIS AFTER FINISHED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = DBHelper(this)

        //region Functions

        // Function for toast message
        fun toastMessage(text: String){
            Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        }

        // Function to display customers
        fun displayCustomers(customerList: ArrayList<Customer>){
            val tvCustomerRecord = findViewById<TextView>(R.id.tvCustomerRecord)
            tvCustomerRecord.text = "Customers\n"
            customerList.forEach {
                tvCustomerRecord.append("$it\n")
            }
        }

        // Function to clear edit text boxes
        fun clearEditTextBoxes(){
            val etId = this.findViewById<EditText>(R.id.etId)
            val etName = this.findViewById<EditText>(R.id.etName)
            val etEmail = this.findViewById<EditText>(R.id.etEmail)
            val etMobile = this.findViewById<EditText>(R.id.etMobile)
            etId.text.clear()
            etName.text.clear()
            etEmail.text.clear()
            etMobile.text.clear()
        }

        // CHECK WITH LECTURER IF THIS IS WHAT HE MEANS
        // Function to initialise default values to database
        fun initialiseDefaultCustomerList(db: DBHelper) {
            db.deleteDB()

            // Create arraylist of default customer records
            val customerList: MutableList<Customer> = ArrayList()

            // Create customers
            val customer1 = Customer(543533, "Banana Bread", "apple@gmail.com", "03541365")
            val customer2 = Customer(654654, "Mango", "apple@gmail.com", "03541365")
            val customer3 = Customer(987598, "Banana", "Kiwi@gmail.com", "04355131")
            val customer4 = Customer(321456, "Apple Pie", "apple@gmail.com", "03541365")
            val customer5 = Customer(784562, "Kiwifruit", "apple@gmail.com", "03541365")

            // Add customers to arraylist
            customerList.add(customer1)
            customerList.add(customer2)
            customerList.add(customer3)
            customerList.add(customer4)
            customerList.add(customer5)

            for (customer in customerList) {
                val id = customer.id
                val name = customer.name
                val email = customer.email.toString()
                val mobile = customer.mobile.toString()
                db.addCustomer(id, name, email, mobile)
            }
            displayCustomers(db.findCustomerRecords())
        }

        // Function to check if an Edit Text box is empty
        fun checkEmptyET(text: String, message: String) : Boolean{
            return if (text.isEmpty()) {
                toastMessage(message)
                true
            } else false
        }

        // Function to check for duplicate ID entries for database integrity
        fun checkForDuplicates(searchItem: String, message: String): Boolean{
            val results = db.findCustomerRecords("search", searchItem, "ID")
            return if (results.isNotEmpty()){
                toastMessage(message)
                true
            } else false
        }

        // Function to get values from edit text boxes
        fun etValues (et: String) : String{
            when (et){
                "id" -> {
                    return findViewById<EditText>(R.id.etId).text.toString()
                }
                "name" -> {
                    return findViewById<EditText>(R.id.etName).text.toString()
                }
                "email" -> {
                    return findViewById<EditText>(R.id.etEmail).text.toString()
                }
                "mobile" -> {
                    return findViewById<EditText>(R.id.etMobile).text.toString()
                }
            }
            return ""
        }

        //endregion

        initialiseDefaultCustomerList(db)

        //region Button to display all customers
        val btnDisplay = findViewById<Button>(R.id.btnDisplay)
        btnDisplay.setOnClickListener {
            displayCustomers(db.findCustomerRecords())
        }
        //endregion

        //region Button to add a new customer record to the database
        val btnAddCustomer = findViewById<Button>(R.id.btnAddNew)
        btnAddCustomer.setOnClickListener {

            val id = etValues("id")
            val name = etValues("name")
            val email = etValues("email")
            val mobile = etValues("mobile")
            var idInt = 0

            // Parse ID to an integer for the database
            try {
                idInt = parseInt(id)
            } catch (e: NumberFormatException) {
                println("Incorrect ID input")
            }

            if (id.length != 6){
                toastMessage("Please enter a 6 digit ID")
                return@setOnClickListener
            }

            if (checkEmptyET(id, "Must have an ID")) return@setOnClickListener
            if (checkEmptyET(name, "Must have a name")) return@setOnClickListener
            if (checkForDuplicates(id, "Duplicate entry found, cannot add")) return@setOnClickListener

            else{
                db.addCustomer(idInt, name, email, mobile)
                toastMessage("$name added to database")
                displayCustomers(db.findCustomerRecords())
                clearEditTextBoxes()
            }
        }
        //endregion

        //region Button to delete customer record by ID
        val btnDeleteCustomer = findViewById<Button>(R.id.btnDeleteCustomer)
        btnDeleteCustomer.setOnClickListener {
            val id = etValues("id")

            val rows = db.deleteCustomer(id)
            toastMessage( when (rows) {
                0 -> "Nothing deleted"
                1 -> "Customer deleted"
                else -> ""
            })
            displayCustomers(db.findCustomerRecords())
            clearEditTextBoxes()
        }
        //endregion222

        //region Button to update customer record by ID
        val btnUpdateCustomer = findViewById<Button>(R.id.btnUpdateCustomer)
        btnUpdateCustomer.setOnClickListener {
            val id = etValues("id")
            val name = etValues("name")
            val email = etValues("email")
            val mobile = etValues("mobile")

            val rows = db.updateCustomer(id, name, email, mobile)
            toastMessage("$rows customers updated")
            displayCustomers(db.findCustomerRecords())
            clearEditTextBoxes()
        }
        //endregion

        //region Button to search for a customer by name
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener {
            val name = etValues("name")

            val results = db.findCustomerRecords("search", name, "NAME")
            if (checkEmptyET(name, "Please enter a name")) return@setOnClickListener
            if (results.isEmpty())
            {
                toastMessage("No customer found")
            }
            else {
                displayCustomers(results)
                toastMessage("$name found")
            }
        }
        //endregion

        //region Button to reset entire database back to default records
        val btnResetDB = findViewById<Button>(R.id.btnResetDB)
        btnResetDB.setOnClickListener {
            db.deleteDB()
            db.close()
            initialiseDefaultCustomerList(db)
            toastMessage("Database has been reset")
            clearEditTextBoxes()
        }
        //endregion

    }//oncreate
}//class