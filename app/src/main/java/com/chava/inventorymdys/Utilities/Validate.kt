package com.chava.inventorymdys

import com.wajahatkarim3.easyvalidation.core.rules.BaseRule

class Validate : BaseRule
{
    // add your validation logic in this method
    override fun validate(text: String) : Boolean
    { // Apply your validation rule logic here
        return text.isNotEmpty()
    }

    // Add your invalid check message here
    override fun getErrorMessage() : String
    {
        return "El campo no debe estar vacio"
    }
}