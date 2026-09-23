package com.misw.sportalarmist.ui.registration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.sportalarmist.R
import com.misw.sportalarmist.data.EnrollmentStore
import com.misw.sportalarmist.data.RegistrationDraft
import com.misw.sportalarmist.data.RegistrationDraftStore
import com.misw.sportalarmist.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private lateinit var draftStore: RegistrationDraftStore
    private lateinit var enrollmentStore: EnrollmentStore
    private var tournamentId: Int = 0
    private var teamId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        draftStore = RegistrationDraftStore(requireContext())
        enrollmentStore = EnrollmentStore(requireContext())
        tournamentId = arguments?.getInt("tournament_id") ?: 0
        teamId = arguments?.getString("team_id") ?: TEAM_TRIANGULO

        binding.appBar.appBarBackIcon.visibility = View.VISIBLE
        binding.appBar.appBarBackIcon.setOnClickListener { findNavController().navigateUp() }

        binding.registrationTitle.text = getString(R.string.title_registration_to_team, teamDisplayName())

        setUpFields()
        setUpButtons()
    }

    private fun setUpFields() {
        binding.nameField.textField.hint = getString(R.string.field_name)
        binding.dorsalField.textField.hint = getString(R.string.field_dorsal)
        binding.teamPasswordField.textField.apply {
            hint = getString(R.string.field_team_password)
            helperText = getString(R.string.helper_team_password)
        }

        val draft = draftStore.load(tournamentId, teamId)
        binding.nameField.textFieldInput.setText(draft.name)
        binding.dorsalField.textFieldInput.setText(draft.dorsal)
        binding.teamPasswordField.textFieldInput.setText(draft.teamPassword)

        val saveOnChange = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = saveDraft()
        }
        binding.nameField.textFieldInput.addTextChangedListener(saveOnChange)
        binding.dorsalField.textFieldInput.addTextChangedListener(saveOnChange)
        binding.teamPasswordField.textFieldInput.addTextChangedListener(saveOnChange)
    }

    private fun setUpButtons() {
        binding.cancelButton.button.setOnClickListener { findNavController().navigateUp() }

        binding.registerButton.button.setBackgroundResource(R.drawable.bg_button_outer_primary)
        binding.registerButton.buttonLabel.apply {
            setBackgroundResource(R.drawable.bg_button_inner_primary)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.button_text_primary))
            text = getString(R.string.action_register)
        }
        binding.registerButton.button.setOnClickListener {
            saveDraft()
            enrollmentStore.markEnrolled(tournamentId, teamId)
            showRegistrationSuccessToast()
            findNavController().navigateUp()
        }
    }

    private fun showRegistrationSuccessToast() {
        val toastView = LayoutInflater.from(requireContext()).inflate(R.layout.toast_success, null)
        Toast(requireContext()).apply {
            duration = Toast.LENGTH_SHORT
            view = toastView
            show()
        }
    }

    private fun saveDraft() {
        draftStore.save(
            tournamentId,
            teamId,
            RegistrationDraft(
                name = binding.nameField.textFieldInput.text?.toString().orEmpty(),
                dorsal = binding.dorsalField.textFieldInput.text?.toString().orEmpty(),
                teamPassword = binding.teamPasswordField.textFieldInput.text?.toString().orEmpty()
            )
        )
    }

    private fun teamDisplayName(): String = when (teamId) {
        TEAM_ESTRELLA -> getString(R.string.team_estrella)
        else -> getString(R.string.team_triangulo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val TEAM_TRIANGULO = "triangulo"
        const val TEAM_ESTRELLA = "estrella"
    }
}
