package com.openclassrooms.realestatemanagerv2.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.realestatemanagerv2.R
import com.openclassrooms.realestatemanagerv2.domain.model.Agent

@Composable
fun AgentSpinner(
    agents: List<Agent>,
    selectedAgent: Agent?,
    onAgentSelected: (Agent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
Column(modifier = Modifier.padding(8.dp)) {
    Text(
        text = stringResource(id = R.string.agent),
        fontSize =  MaterialTheme.typography.titleMedium.fontSize,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .clickable(onClick = { expanded = true })
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedAgent?.name ?: "Select an agent",
                modifier = Modifier.wrapContentWidth()
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            agents.forEach { agent ->
                DropdownMenuItem(onClick = {
                    onAgentSelected(agent)
                    expanded = false
                },
                    text = { Text(text = agent.name) }
                )
            }
        }
    }
}
    
}

@Preview(showBackground = true, backgroundColor = -1)
@Composable
fun AgentSpinnerPreview() {
    AgentSpinner(agents = listOf(
        Agent("1", "AgentTest1", "0111111111", "agent1@gmail.com"),
        Agent("2", "AgentTest2", "0222222222", "agent2@gmail.com"),
        Agent("3", "AgentTest3", "0333333333", "agent3@gmail.com")),
        selectedAgent = null,
        onAgentSelected = {})
}