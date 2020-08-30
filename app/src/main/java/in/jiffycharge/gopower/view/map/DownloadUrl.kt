package `in`.jiffycharge.gopower.view.map

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class DownloadUrl {

    fun readUrl(strUrl:String):String
    {
        var data:String?=null
        var iStream:InputStream?=null
        var urlConnection:HttpURLConnection?=null

        try {

            val url=URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection=url.openConnection()as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream=urlConnection.inputStream
           val  allText =iStream.bufferedReader().use(BufferedReader::readText)

//            val br=BufferedReader(InputStreamReader(iStream))
//            val sb=StringBuffer()
//            var line:String=""
//            while ( br.readLine()!=null)
//            {
//                line=br.readLine()
//                sb.append(line)
//            }
            data = if (allText.toString().startsWith("\"")) {
                allText.toString().substring(1,allText.toString().length)

            }else {
                allText.toString()

            }



        }catch (e:Exception)
        {
            e.printStackTrace()
        }finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data.toString()

    }
}