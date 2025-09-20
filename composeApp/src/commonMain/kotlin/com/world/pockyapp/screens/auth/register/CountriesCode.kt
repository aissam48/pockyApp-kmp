package com.world.pockyapp.screens.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

data class CountryPhoneCode(
    val country: String,
    val code: String,
    val flag: String = ""
)

object CountryPhoneCodesData {
    fun getCountryPhoneCodes(): List<CountryPhoneCode> {
        return listOf(
            CountryPhoneCode("Morocco", "+212", "🇲🇦"),
            CountryPhoneCode("United States", "+1", "🇺🇸"),
            CountryPhoneCode("Canada", "+1", "🇨🇦"),
            CountryPhoneCode("United Kingdom", "+44", "🇬🇧"),
            CountryPhoneCode("France", "+33", "🇫🇷"),
            CountryPhoneCode("Germany", "+49", "🇩🇪"),
            CountryPhoneCode("Spain", "+34", "🇪🇸"),
            CountryPhoneCode("Italy", "+39", "🇮🇹"),
            CountryPhoneCode("Japan", "+81", "🇯🇵"),
            CountryPhoneCode("South Korea", "+82", "🇰🇷"),
            CountryPhoneCode("China", "+86", "🇨🇳"),
            CountryPhoneCode("India", "+91", "🇮🇳"),
            CountryPhoneCode("Australia", "+61", "🇦🇺"),
            CountryPhoneCode("Brazil", "+55", "🇧🇷"),
            CountryPhoneCode("Mexico", "+52", "🇲🇽"),
            CountryPhoneCode("Argentina", "+54", "🇦🇷"),
            CountryPhoneCode("Russia", "+7", "🇷🇺"),
            CountryPhoneCode("Turkey", "+90", "🇹🇷"),
            CountryPhoneCode("Egypt", "+20", "🇪🇬"),
            CountryPhoneCode("Saudi Arabia", "+966", "🇸🇦"),
            CountryPhoneCode("United Arab Emirates", "+971", "🇦🇪"),
            CountryPhoneCode("Qatar", "+974", "🇶🇦"),
            CountryPhoneCode("Kuwait", "+965", "🇰🇼"),
            CountryPhoneCode("Oman", "+968", "🇴🇲"),
            CountryPhoneCode("Bahrain", "+973", "🇧🇭"),
            CountryPhoneCode("Jordan", "+962", "🇯🇴"),
            CountryPhoneCode("Lebanon", "+961", "🇱🇧"),
            CountryPhoneCode("Syria", "+963", "🇸🇾"),
            CountryPhoneCode("Iraq", "+964", "🇮🇶"),
            CountryPhoneCode("Iran", "+98", "🇮🇷"),
            CountryPhoneCode("Afghanistan", "+93", "🇦🇫"),
            CountryPhoneCode("Pakistan", "+92", "🇵🇰"),
            CountryPhoneCode("Bangladesh", "+880", "🇧🇩"),
            CountryPhoneCode("Sri Lanka", "+94", "🇱🇰"),
            CountryPhoneCode("Nepal", "+977", "🇳🇵"),
            CountryPhoneCode("Myanmar", "+95", "🇲🇲"),
            CountryPhoneCode("Thailand", "+66", "🇹🇭"),
            CountryPhoneCode("Vietnam", "+84", "🇻🇳"),
            CountryPhoneCode("Cambodia", "+855", "🇰🇭"),
            CountryPhoneCode("Laos", "+856", "🇱🇦"),
            CountryPhoneCode("Malaysia", "+60", "🇲🇾"),
            CountryPhoneCode("Singapore", "+65", "🇸🇬"),
            CountryPhoneCode("Indonesia", "+62", "🇮🇩"),
            CountryPhoneCode("Philippines", "+63", "🇵🇭"),
            CountryPhoneCode("Brunei", "+673", "🇧🇳"),
            CountryPhoneCode("Timor-Leste", "+670", "🇹🇱"),
            CountryPhoneCode("Papua New Guinea", "+675", "🇵🇬"),
            CountryPhoneCode("New Zealand", "+64", "🇳🇿"),
            CountryPhoneCode("Fiji", "+679", "🇫🇯"),
            CountryPhoneCode("South Africa", "+27", "🇿🇦"),
            CountryPhoneCode("Nigeria", "+234", "🇳🇬"),
            CountryPhoneCode("Kenya", "+254", "🇰🇪"),
            CountryPhoneCode("Ghana", "+233", "🇬🇭"),
            CountryPhoneCode("Ethiopia", "+251", "🇪🇹"),
            CountryPhoneCode("Tanzania", "+255", "🇹🇿"),
            CountryPhoneCode("Uganda", "+256", "🇺🇬"),
            CountryPhoneCode("Rwanda", "+250", "🇷🇼"),
            CountryPhoneCode("Burundi", "+257", "🇧🇮"),
            CountryPhoneCode("Democratic Republic of Congo", "+243", "🇨🇩"),
            CountryPhoneCode("Republic of Congo", "+242", "🇨🇬"),
            CountryPhoneCode("Central African Republic", "+236", "🇨🇫"),
            CountryPhoneCode("Chad", "+235", "🇹🇩"),
            CountryPhoneCode("Cameroon", "+237", "🇨🇲"),
            CountryPhoneCode("Equatorial Guinea", "+240", "🇬🇶"),
            CountryPhoneCode("Gabon", "+241", "🇬🇦"),
            CountryPhoneCode("São Tomé and Príncipe", "+239", "🇸🇹"),
            CountryPhoneCode("Cape Verde", "+238", "🇨🇻"),
            CountryPhoneCode("Guinea-Bissau", "+245", "🇬🇼"),
            CountryPhoneCode("Guinea", "+224", "🇬🇳"),
            CountryPhoneCode("Sierra Leone", "+232", "🇸🇱"),
            CountryPhoneCode("Liberia", "+231", "🇱🇷"),
            CountryPhoneCode("Côte d'Ivoire", "+225", "🇨🇮"),
            CountryPhoneCode("Burkina Faso", "+226", "🇧🇫"),
            CountryPhoneCode("Mali", "+223", "🇲🇱"),
            CountryPhoneCode("Niger", "+227", "🇳🇪"),
            CountryPhoneCode("Senegal", "+221", "🇸🇳"),
            CountryPhoneCode("Gambia", "+220", "🇬🇲"),
            CountryPhoneCode("Mauritania", "+222", "🇲🇷"),
            CountryPhoneCode("Algeria", "+213", "🇩🇿"),
            CountryPhoneCode("Tunisia", "+216", "🇹🇳"),
            CountryPhoneCode("Libya", "+218", "🇱🇾"),
            CountryPhoneCode("Sudan", "+249", "🇸🇩"),
            CountryPhoneCode("South Sudan", "+211", "🇸🇸"),
            CountryPhoneCode("Eritrea", "+291", "🇪🇷"),
            CountryPhoneCode("Djibouti", "+253", "🇩🇯"),
            CountryPhoneCode("Somalia", "+252", "🇸🇴"),
            CountryPhoneCode("Comoros", "+269", "🇰🇲"),
            CountryPhoneCode("Seychelles", "+248", "🇸🇨"),
            CountryPhoneCode("Mauritius", "+230", "🇲🇺"),
            CountryPhoneCode("Madagascar", "+261", "🇲🇬"),
            CountryPhoneCode("Réunion", "+262", "🇷🇪"),
            CountryPhoneCode("Mayotte", "+262", "🇾🇹"),
            CountryPhoneCode("Norway", "+47", "🇳🇴"),
            CountryPhoneCode("Sweden", "+46", "🇸🇪"),
            CountryPhoneCode("Denmark", "+45", "🇩🇰"),
            CountryPhoneCode("Finland", "+358", "🇫🇮"),
            CountryPhoneCode("Iceland", "+354", "🇮🇸"),
            CountryPhoneCode("Netherlands", "+31", "🇳🇱"),
            CountryPhoneCode("Belgium", "+32", "🇧🇪"),
            CountryPhoneCode("Luxembourg", "+352", "🇱🇺"),
            CountryPhoneCode("Switzerland", "+41", "🇨🇭"),
            CountryPhoneCode("Austria", "+43", "🇦🇹"),
            CountryPhoneCode("Czech Republic", "+420", "🇨🇿"),
            CountryPhoneCode("Slovakia", "+421", "🇸🇰"),
            CountryPhoneCode("Hungary", "+36", "🇭🇺"),
            CountryPhoneCode("Poland", "+48", "🇵🇱"),
            CountryPhoneCode("Lithuania", "+370", "🇱🇹"),
            CountryPhoneCode("Latvia", "+371", "🇱🇻"),
            CountryPhoneCode("Estonia", "+372", "🇪🇪"),
            CountryPhoneCode("Belarus", "+375", "🇧🇾"),
            CountryPhoneCode("Ukraine", "+380", "🇺🇦"),
            CountryPhoneCode("Moldova", "+373", "🇲🇩"),
            CountryPhoneCode("Romania", "+40", "🇷🇴"),
            CountryPhoneCode("Bulgaria", "+359", "🇧🇬"),
            CountryPhoneCode("Greece", "+30", "🇬🇷"),
            CountryPhoneCode("Cyprus", "+357", "🇨🇾"),
            CountryPhoneCode("Malta", "+356", "🇲🇹"),
            CountryPhoneCode("Albania", "+355", "🇦🇱"),
            CountryPhoneCode("Montenegro", "+382", "🇲🇪"),
            CountryPhoneCode("Serbia", "+381", "🇷🇸"),
            CountryPhoneCode("Bosnia and Herzegovina", "+387", "🇧🇦"),
            CountryPhoneCode("Croatia", "+385", "🇭🇷"),
            CountryPhoneCode("Slovenia", "+386", "🇸🇮"),
            CountryPhoneCode("North Macedonia", "+389", "🇲🇰"),
            CountryPhoneCode("Kosovo", "+383", "🇽🇰"),
            CountryPhoneCode("Portugal", "+351", "🇵🇹"),
            CountryPhoneCode("Andorra", "+376", "🇦🇩"),
            CountryPhoneCode("Monaco", "+377", "🇲🇨"),
            CountryPhoneCode("San Marino", "+378", "🇸🇲"),
            CountryPhoneCode("Vatican City", "+39", "🇻🇦"),
            CountryPhoneCode("Liechtenstein", "+423", "🇱🇮")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPhoneCodeDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCountrySelected: (CountryPhoneCode) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }
    val countryCodes = CountryPhoneCodesData.getCountryPhoneCodes()

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Country Code",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { onDismiss() }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Search countries or codes...",
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = Color(0xFFDFC46B),
                            focusedBorderColor = Color(0xFFDFC46B),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            backgroundColor = Color(0xFFF8F9FA)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Countries List
                    LazyColumn {
                        items(
                            countryCodes.filter { country ->
                                country.country.lowercase()
                                    .contains(searchQuery.value.lowercase()) ||
                                        country.code.contains(searchQuery.value)
                            }
                        ) { countryCode ->
                            CountryPhoneCodeItem(
                                countryCode = countryCode,
                                onSelected = { onCountrySelected(countryCode) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryPhoneCodeItem(
    countryCode: CountryPhoneCode,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelected() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag
            Text(
                text = countryCode.flag,
                fontSize = 24.sp,
                modifier = Modifier.width(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Country name
            Text(
                text = countryCode.country,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Phone code
            Text(
                text = countryCode.code,
                color = Color(0xFFDFC46B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Usage Example in your RegisterScreen:
@Composable
fun PhoneNumberFieldWithCountryCode(selectedCode: (CountryPhoneCode, phone: String) -> Unit) {
    val selectedCountryCode = remember { mutableStateOf(CountryPhoneCode("Morocco", "+212", "🇲🇦")) }
    val showCountryCodeDialog = remember { mutableStateOf(false) }
    val phoneNumber = remember { mutableStateOf("") }

    OutlinedTextField(
        value = phoneNumber.value,
        onValueChange = {
            phoneNumber.value = it
            selectedCode(selectedCountryCode.value, phoneNumber.value)
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.Black,
            cursorColor = Color(0xFFDFC46B),
            focusedBorderColor = Color(0xFFDFC46B),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            backgroundColor = Color(0xFFF8F9FA)
        ),
        label = { Text(text = "Phone Number", color = Color.Gray) },
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { showCountryCodeDialog.value = true }
            ) {
                Text(
                    text = selectedCountryCode.value.flag,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedCountryCode.value.code,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFFDFC46B))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    )

    CountryPhoneCodeDialog(
        showDialog = showCountryCodeDialog.value,
        onDismiss = { showCountryCodeDialog.value = false },
        onCountrySelected = { countryCode ->
            selectedCountryCode.value = countryCode
            showCountryCodeDialog.value = false
        }
    )
}