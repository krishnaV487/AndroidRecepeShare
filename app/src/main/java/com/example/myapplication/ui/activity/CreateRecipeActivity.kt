package com.example.myapplication.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.MembersAdapter
import com.example.myapplication.R
import com.example.myapplication.SearchResultAdapter
import com.example.myapplication.databinding.ActivityCreateRecipeBinding
import com.example.myapplication.db.Recipe
import com.example.myapplication.db.Ingredient
import com.example.myapplication.firestore.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRecipeBinding
    private lateinit var recipeRepository: RecipeRepository
    private var imagePath: String = "" // For storing image path
    private val ingredientRows = mutableListOf<View>()
    private val stepRows = mutableListOf<View>()
    private lateinit var userSearchAdapter: SearchResultAdapter
    private lateinit var groupSearchAdapter: SearchResultAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val selectedUsers = mutableListOf<Pair<String, String>>()
    private val selectedGroups = mutableListOf<Pair<String, String>>()
    private val IMAGE_PICK_CODE = 1000

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        recipeRepository = RecipeRepository(this)
//// Setup adapters and search fields
//        setupSearchAdapters()
//        setupSearchListeners()
//
//        val recipeId = intent.getStringExtra("recipeId")
//
//        if (recipeId != null) {
//            setupEditRecipe(recipeId) // Load existing recipe data
//            setupStepsSection()
//            binding.submitRecipeButton.text = "Edit Recipe"
//        } else {
//            setupIngredientSection()
//            setupStepsSection()
//            binding.submitRecipeButton.text = "Create Recipe"
//        }
//
//        binding.submitRecipeButton.setOnClickListener {
//            if (recipeId != null) {
//                editRecipe(recipeId) // Update recipe
//            } else {
//                createRecipe() // Create new recipe
//            }        }
//
//        binding.addImageButton.setOnClickListener {
//            // Placeholder for image selection logic
//            Toast.makeText(this, "Image selection coming soon!", Toast.LENGTH_SHORT).show()
//            imagePath = "placeholder_image_path" // Simulated path
//        }
//        setupIngredientSection()
//
//    }
@SuppressLint("SetTextI18n")
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCreateRecipeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    recipeRepository = RecipeRepository(this)

    setupSearchAdapters()
    setupSearchListeners()
    setupIngredientSection()
    setupStepsSection()

    val recipeId = intent.getStringExtra("recipeId")
    if (recipeId != null) {
        setupEditRecipe(recipeId)
        binding.submitRecipeButton.text = "Edit Recipe"
    } else {
        binding.submitRecipeButton.text = "Create Recipe"
    }
        binding.addImageButton.setOnClickListener {
            // Placeholder for image selection logic
            binding.addImageButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/*" // Allow only image selection
                }
                startActivityForResult(intent, IMAGE_PICK_CODE)
            }
            imagePath = "placeholder_image_path" // Simulated path
        }
    binding.submitRecipeButton.setOnClickListener {
        if (recipeId != null) {
            editRecipe(recipeId)
        } else {
            createRecipe()
        }
    }
}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            imagePath = uri.toString() // Store the image URI as a string

            // Display the selected image in an ImageView
            binding.recipeImageView.apply {
                visibility = View.VISIBLE
                setImageURI(uri)
            }
        }
    }


    private fun setupSearchAdapters() {
        // Search Result Adapter for Users
        userSearchAdapter = SearchResultAdapter { id, name ->
            selectedUsers.add(id to name)
            updateSelectedUsers()
            binding.userSearchRecyclerView.visibility = View.GONE
        }
        binding.userSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = userSearchAdapter
        }

        // Search Result Adapter for Groups
        groupSearchAdapter = SearchResultAdapter { id, name ->
            selectedGroups.add(id to name)
            updateSelectedGroups()
            binding.groupSearchRecyclerView.visibility = View.GONE
        }
        binding.groupSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = groupSearchAdapter
        }
    }

    private fun setupSearchListeners() {
        // Search Users
        binding.userSearchEditText.addTextChangedListener { query ->
            searchUsers(query.toString())
        }

        // Search Groups
        binding.groupSearchEditText.addTextChangedListener { query ->
            searchGroups(query.toString())
        }
    }

    private fun searchUsers(query: String) {
        if (query.isBlank()) return

        lifecycleScope.launch {
            val results = firestore.collection("users")
                .whereGreaterThanOrEqualTo("firstName", query)
                .get()
                .await()
                .documents.mapNotNull {
                    val id = it.id
                    val name = "${it.getString("firstName")} ${it.getString("lastName")}"
                    id to name
                }

            userSearchAdapter.submitList(results)
            binding.userSearchRecyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun searchGroups(query: String) {
        if (query.isBlank()) return

        lifecycleScope.launch {
            val results = firestore.collection("groups")
                .whereGreaterThanOrEqualTo("groupName", query)
                .get()
                .await()
                .documents.mapNotNull {
                    val id = it.id
                    val name = it.getString("groupName") ?: "Unknown Group"
                    id to name
                }

            groupSearchAdapter.submitList(results)
            binding.groupSearchRecyclerView.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun updateSelectedUsers() {
        binding.selectedUsersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = MembersAdapter().apply {
                submitList(selectedUsers.map { Member(it.first, it.second) })
            }
        }
    }

    private fun updateSelectedGroups() {
        binding.selectedGroupsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CreateRecipeActivity)
            adapter = MembersAdapter().apply {
                submitList(selectedGroups.map { Member(it.first, it.second) })
            }
        }
    }


    private fun setupStepsSection() {
        val container = binding.stepsContainer

        // Add initial step row
        addStepRow(container)

        binding.addStepButton.setOnClickListener {
            addStepRow(container)
        }
    }


    private fun addStepRow(container: LinearLayout) {
        val row = layoutInflater.inflate(R.layout.step_row, container, false)

        val stepDescription = row.findViewById<EditText>(R.id.stepDescription)
        val actionButton = row.findViewById<Button>(R.id.stepActionButton)

        actionButton.setOnClickListener {
            if (actionButton.text == "+") {
                addStepRow(container)
            } else {
                container.removeView(row)
                stepRows.remove(row)
                updateStepButtons()
            }
        }

        container.addView(row)
        stepRows.add(row)
        updateStepButtons()
    }
    private fun updateStepButtons() {
        for ((index, row) in stepRows.withIndex()) {
            val button = row.findViewById<Button>(R.id.stepActionButton)
            button.text = if (index == stepRows.size - 1) "+" else "-"
        }
    }

    private fun getStepsData(container: LinearLayout): List<String> {
        val steps = mutableListOf<String>()
        for (i in 0 until container.childCount) {
            val row = container.getChildAt(i)
            val description = row.findViewById<EditText>(R.id.stepDescription).text.toString()
            if (description.isNotEmpty()) {
                steps.add(description)
            }
        }
        return steps
    }
    private fun setupEditRecipe(recipeId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val recipe = recipeRepository.getRecipeById(recipeId)
            recipe?.let {
                withContext(Dispatchers.Main) {
                    binding.titleEditText.setText(it.title)
                    binding.descriptionEditText.setText(it.description)
                    binding.isPublicSwitch.isChecked = it.isPublic
                    binding.groupSearchEditText.setText(it.groupIdList.joinToString(", "))
                    binding.userSearchEditText.setText(it.userIdList.joinToString(", "))

                    // Populate existing ingredients
                    setupIngredientSection()
                    it.ingredients.forEach { ingredient ->
                        addIngredientRowWithValues(
                            ingredient.name,
                            ingredient.amount.split(" ")[0], // Extract numeric value
                            ingredient.amount.split(" ")[1] // Extract unit
                        )
                    }

                    // Populate existing steps
                    setupStepsSection() // Ensure steps section is initialized
                    it.steps.forEach { step ->
                        addStepRowWithValue(step) // Add each step dynamically
                    }
                }
            }
        }
    }
    private fun addStepRowWithValue(stepDescriptionValue: String) {
        val row = layoutInflater.inflate(R.layout.step_row, binding.stepsContainer, false)

        val stepDescription = row.findViewById<EditText>(R.id.stepDescription)
        val actionButton = row.findViewById<Button>(R.id.stepActionButton)

        // Populate the step description
        stepDescription.setText(stepDescriptionValue)

        actionButton.setOnClickListener {
            if (actionButton.text == "+") {
                addStepRow(binding.stepsContainer) // Add new row
            } else {
                binding.stepsContainer.removeView(row)
                stepRows.remove(row)
                updateStepButtons()
            }
        }

        binding.stepsContainer.addView(row)
        stepRows.add(row)
        updateStepButtons()
    }


    private fun addIngredientRowWithValues(name: String, amount: String, unit: String) {
        val row = layoutInflater.inflate(R.layout.ingredient_row, binding.ingredientsContainer, false)

        val nameInput = row.findViewById<EditText>(R.id.ingredientName)
        val amountInput = row.findViewById<EditText>(R.id.ingredientAmount)
        val unitSpinner = row.findViewById<Spinner>(R.id.ingredientUnit)
        val actionButton = row.findViewById<Button>(R.id.ingredientActionButton)

        // Populate Spinner with units
        val units = listOf("Cup", "Gram", "Kg", "Table Spoon", "Tea Spoon", "Pc", "L", "Ml", "Pnch")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter

        // Set values for the row
        nameInput.setText(name)
        amountInput.setText(amount)

        // Set Spinner selection based on the unit
        val position = units.indexOf(unit)
        if (position >= 0) {
            unitSpinner.setSelection(position)
        }

        // Action button to remove row
        actionButton.setOnClickListener {
            binding.ingredientsContainer.removeView(row)
            ingredientRows.remove(row)
            updateActionButtons()
        }

        binding.ingredientsContainer.addView(row)
        ingredientRows.add(row)
        updateActionButtons()
    }

    private fun editRecipe(recipeId: String) {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val isPublic = binding.isPublicSwitch.isChecked
        val steps = getStepsData(binding.stepsContainer) // Collect steps

        val ingredients = getIngredientData(binding.ingredientsContainer)

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val updatedRecipe = Recipe(
                recipeId = recipeId,
                title = title,
                description = description,
                isPublic = isPublic,
                groupIdList = selectedGroups.map { it.first },
                userIdList = selectedUsers.map { it.first },
                createdBy = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                timestamp = System.currentTimeMillis(),
                recipeImage = imagePath,
                ingredients = ingredients,
                steps = steps // Placeholder
            )

            lifecycleScope.launch(Dispatchers.IO) {
                recipeRepository.insertRecipe(updatedRecipe) // Replaces the existing recipe
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreateRecipeActivity, "Recipe Updated!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            }
        } else {
            Toast.makeText(this, "Title and Description are required!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createRecipe() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val isPublic = binding.isPublicSwitch.isChecked
        val steps = getStepsData(binding.stepsContainer) // Collect steps

        if (title.isNotEmpty() && description.isNotEmpty()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val recipeId = UUID.randomUUID().toString()

            val ingredients = getIngredientData(binding.ingredientsContainer)

            val recipe = Recipe(
                recipeId = recipeId,
                title = title,
                description = description,
                isPublic = isPublic,
                groupIdList = selectedGroups.map { it.first },
                userIdList = selectedUsers.map { it.first },
                createdBy = userId,
                timestamp = System.currentTimeMillis(),
                recipeImage = imagePath,
                ingredients = ingredients, // Capture ingredients from dynamic rows
                steps = steps // Placeholder for steps
            )


            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val repository = RecipeRepository(this@CreateRecipeActivity)
                    repository.insertRecipe(recipe)

                    runOnUiThread {
                        Toast.makeText(this@CreateRecipeActivity, "Recipe Created!", Toast.LENGTH_SHORT).show()
                        navigateToMain()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CreateRecipeActivity, "Failed to create recipe!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Title and Description are required!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setupIngredientSection() {
        val units = listOf("Cup", "Gram", "Kg", "Table Spoon", "Tea Spoon", "Pc", "L", "Ml", "Pnch") // Units for dropdown
        val container = binding.ingredientsContainer

        // Add initial row
        addIngredientRow(container, units)

    }

    private fun addIngredientRow(container: LinearLayout, units: List<String>) {
        val row = layoutInflater.inflate(R.layout.ingredient_row, container, false)

        val nameInput = row.findViewById<EditText>(R.id.ingredientName)
        val amountInput = row.findViewById<EditText>(R.id.ingredientAmount)
        val unitSpinner = row.findViewById<Spinner>(R.id.ingredientUnit)
        val actionButton = row.findViewById<Button>(R.id.ingredientActionButton)

        // Populate Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = adapter

        actionButton.setOnClickListener {
            if (actionButton.text == "+") {
                addIngredientRow(container, units) // Add new row
            } else {
                container.removeView(row) // Remove current row
                ingredientRows.remove(row)
                updateActionButtons()
            }
        }

        container.addView(row)
        ingredientRows.add(row)
        updateActionButtons()
    }





    private fun updateActionButtons() {
        for ((index, row) in ingredientRows.withIndex()) {
            val button = row.findViewById<Button>(R.id.ingredientActionButton)
            button.text = if (index == ingredientRows.size - 1) "+" else "-"
        }
    }

    private fun getIngredientData(container: LinearLayout): List<Ingredient> {
        val ingredients = mutableListOf<Ingredient>()

        for (i in 0 until container.childCount) {
            val row = container.getChildAt(i)
            val name = row.findViewById<EditText>(R.id.ingredientName).text.toString()
            val amount = row.findViewById<EditText>(R.id.ingredientAmount).text.toString()
            val unit = row.findViewById<Spinner>(R.id.ingredientUnit).selectedItem.toString()

            if (name.isNotEmpty() && amount.isNotEmpty()) {
                ingredients.add(Ingredient(name, "$amount $unit"))
            }
        }
        return ingredients
    }


}
