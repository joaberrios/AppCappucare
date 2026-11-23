package com.example.appcappucare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Informacion recibida desde la API externa.
data class RespuestaMedicamentos(
    val results: List<MedicamentoExterno> = emptyList()
)

data class MedicamentoExterno(
    val openfda: DatosMedicamento = DatosMedicamento(),
    val purpose: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

data class DatosMedicamento(
    val brand_name: List<String> = emptyList(),
    val generic_name: List<String> = emptyList(),
    val manufacturer_name: List<String> = emptyList()
)

// Endpoints de openFDA.
interface MedicamentoApi {

    @GET("drug/label.json")
    suspend fun buscarMedicamentos(
        @Query("search") busqueda: String,
        @Query("limit") limite: Int = 5
    ): RespuestaMedicamentos
}

// Conexion independiente del backend CappuCare.
object ClienteMedicamentos {

    val api: MedicamentoApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.fda.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedicamentoApi::class.java)
    }
}
