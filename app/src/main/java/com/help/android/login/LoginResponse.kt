package com.help.android.login

import java.io.Serializable


data class  GETOTPRequestParams(

        val mobile:String?

): BaseModel()
data class GetOTPResponse(
        val txnId : String?

): Serializable


data class  VerifyOTPRequestParams(

        val otp:String?,
        val txnId:String?

): BaseModel()

data class VerifyOtpResponse(
        val token : String?

): Serializable


data class CustomerLoginResponse(
        val id : String?,
        val email: String?,
        val first_name:String?,
        val last_name:String?,
        val username:String?,
        val avatar_url:String?


): Serializable
data class  CustomerRegisterRequestParams(
        val email: String?,
        val first_name:String?,
        val last_name:String?,
        val username:String?,
        val password:String?

): BaseModel()
data class CustomerRegisterResponse(
        val id : String?,
        val email: String?,
        val first_name:String?,
        val last_name:String?,
        val username:String?,
        val avatar_url:String?


): Serializable

data class CustomerAddressResponse(
        val id : String?,
        val email: String?,
        val first_name:String?,
        val last_name:String?,
        val username:String?


): Serializable
data class  CustomerAddressRequestParams(
        val shipping: List<Shipping>

): BaseModel()
data class Shipping(
        var first_name: String?,
var last_name: String?,
var company: String?,
var address_1: String? ,
var address_2: String?,
var city: String?,
var postcode: String?,
var country:String?,
var state: String?

):Serializable

data class Billing(
        var first_name: String?,
        var last_name: String? ,
        var company: String?,
        var address_1: String? ,
        var address_2: String?,
        var city: String? ,
        var postcode: String?,
        var country:String? ,
        var state: String? ,
        var phone: String?

):Serializable

data class CustomerDetailResponse(
        val id : Int?,
        val email: String?,
        val first_name:String?,
        val last_name:String?,
        val username:String?,
        val avatar_url:String?,
        val shipping: Shipping?,
        val billing: Billing?

): Serializable



open class BaseModel {

}
