package com.br.apsmobile.helper

import java.text.*
import java.util.*

class GetMask {

    companion object {

        private const val DIA_MES: Int = 1
        private const val DIA_MES_HORA: Int = 2
        private const val DIA_MES_ANO: Int = 3
        private const val DIA_MES_ANO_HORA: Int = 4
        private const val HORA: Int = 5
        private const val HORA_DIA_MES_ANO: Int = 6

        fun getValue(value: Double): String {
            val nf: NumberFormat = DecimalFormat(
                "#,##0.00",
                DecimalFormatSymbols(Locale("pt", "BR"))
            )
            return nf.format(value)
        }

        fun getDate(time: Long, tipo: Int): String {

            // 1 -> dia/mes (26 outubro)
            // 2 -> dia/mes hora:minuto (26/10 às 07:45)
            // 3 -> dia/mes/ano
            // 4 -> dia/mes/ano hora:minuto (26/10/2020 às 07:45)
            // 5 -> hora:minuto (22:56)
            // 6 -> hora:minuto - dia/mes/ano (21:37 - 08/12/2020)

            val locale = Locale("pt", "BR")
            val fuso = "America/Sao_Paulo"

            val diaSdf = SimpleDateFormat("dd", locale)
            diaSdf.timeZone = TimeZone.getTimeZone(fuso)

            val mesSdf = SimpleDateFormat("MM", locale)
            mesSdf.timeZone = TimeZone.getTimeZone(fuso)

            val anoSdf = SimpleDateFormat("yyyy", locale)
            anoSdf.timeZone = TimeZone.getTimeZone(fuso)

            val horaSdf = SimpleDateFormat("HH", locale)
            horaSdf.timeZone = TimeZone.getTimeZone(fuso)

            val minutoSdf = SimpleDateFormat("mm", locale)
            minutoSdf.timeZone = TimeZone.getTimeZone(fuso)

            val dateFormat = DateFormat.getDateTimeInstance()
            val netDate = Date(time)
            dateFormat.format(netDate)

            val dia = diaSdf.format(netDate)
            var mes = mesSdf.format(netDate)
            val ano = anoSdf.format(netDate)

            val hora = horaSdf.format(netDate)
            val minuto = minutoSdf.format(netDate)

            if (tipo == DIA_MES) {
                mes = when (mes) {
                    "01" -> "janeiro"
                    "02" -> "fevereiro"
                    "03" -> "março"
                    "04" -> "abril"
                    "05" -> "maio"
                    "06" -> "junho"
                    "07" -> "julho"
                    "08" -> "agosto"
                    "09" -> "setembro"
                    "10" -> "outubro"
                    "11" -> "novembro"
                    "12" -> "novembro"
                    else -> ""
                }
            }

            return when (tipo) {
                DIA_MES -> "${dia}/${mes}"
                DIA_MES_HORA -> "${dia}/${mes} às ${hora}:${minuto}"
                DIA_MES_ANO -> "${dia}/${mes}/${ano}"
                DIA_MES_ANO_HORA -> "${dia}/${mes}/${ano} às ${hora}:${minuto}"
                HORA -> "${hora}:${minuto}"
                HORA_DIA_MES_ANO -> "${hora}:${minuto} - ${dia}/${mes}/${ano}"
                else -> {
                    ""
                }
            }

        }

    }

}