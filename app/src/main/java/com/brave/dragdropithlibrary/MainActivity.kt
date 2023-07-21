package com.brave.dragdropithlibrary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.brave.dragdropithlibrary.databinding.ActivityDragListBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityDragListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDragListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        if (savedInstanceState == null) {
            showFragment(BoardFragment.newInstance())
        }
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(binding.container.id, fragment, "fragment").commit()
    }
}
