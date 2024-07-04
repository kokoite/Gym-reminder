package com.example.gymreminder


//    private fun configureSearchView() {
//        searchView = binding.searchView
//        closeButton = binding.closeIcon
//        filterButton = binding.filterIcon
//        searchButton = binding.searchIcon
//        searchView.addTextChangedListener { text ->
//            if(text?.isNotEmpty() == true) {
//                closeButton.visibility = View.VISIBLE
//                closeButton.setBackgroundTint(R.color.black)
//                searchButton.setBackgroundTint(R.color.black)
//                filterButton.setBackgroundTint(R.color.black)
//                if(text.toString().last() == '\n') {
//
//                    viewModel.filterUser(UserFilter.NameFilter(text.toString()))
//                    val txt = text.toString().filter {
//                        it != '\n'
//                    }
//                    searchView.setText(txt)
//                    searchView.clearFocus()
//                } else {
//                    viewModel.filterUser(UserFilter.NameFilter(text.toString()))
//                }
//            } else {
//                closeButton.visibility = View.INVISIBLE
//            }
//
//            closeButton.setOnClickListener {
//                searchView.setText("")
//                it.visibility = View.GONE
//                searchButton.setBackgroundTint(R.color.lightGray)
//                filterButton.setBackgroundTint(R.color.lightGray)
//                closeButton.setBackgroundTint(R.color.lightGray)
//            }
//        }
//
//
//        filterButton.setOnClickListener {
//            showBottomSheet()
//        }
//
//        searchButton.setOnClickListener {
//            val text = searchView.text.toString()
//            if(text.length > 1) {
//                viewModel.filterUser(UserFilter.NameFilter(text))
//            }
//        }
//    }


/*

<EditText
        android:id="@+id/searchView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:hint="Search for users here"
        android:paddingVertical="8dp"
        android:paddingStart="52dp"
        android:gravity="center_vertical"
        android:layout_marginEnd="10dp"
        android:layout_marginTop="12dp"
        android:elevation="12dp"
        android:lines="1"
        android:focusedByDefault="false"
        android:background="@drawable/home_search_field_background"
        />

    <ImageView
        android:id="@+id/search_icon"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:src="@drawable/search_icon"
        app:tint="@color/lightGray"
        app:layout_constraintStart_toStartOf="@id/searchView"
        app:layout_constraintTop_toTopOf="@id/searchView"
        app:layout_constraintBottom_toBottomOf="@id/searchView"
        android:layout_marginStart="15dp"
        android:elevation="12dp"
        />

    <ImageView
        android:id="@+id/filter_icon"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:src="@drawable/filter_list"
        app:tint="@color/lightGray"
        app:layout_constraintTop_toTopOf="@id/searchView"
        app:layout_constraintBottom_toBottomOf="@id/searchView"
        app:layout_constraintEnd_toEndOf="@+id/searchView"
        android:layout_marginEnd="20dp"
        android:elevation="12dp"
        />

    <ImageView
        android:id="@+id/close_icon"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:src="@drawable/close_icon"
        app:tint="@color/lightGray"
        app:layout_constraintEnd_toStartOf="@id/filter_icon"
        app:layout_constraintTop_toTopOf="@id/searchView"
        app:layout_constraintBottom_toBottomOf="@id/searchView"
        android:visibility="invisible"
        android:layout_marginEnd="20dp"
        android:elevation="12dp"
        />


 */


