package com.example.gymreminder.repository

class UserRepositoryImpl(private val userDataSource: UserDataSource, private val userRemoteDataSource: UserRemoteDataSource): UserRepository {
    override fun fetchAllUsersLocally() {
        userDataSource.fetchAllUsers()
    }

    override fun fetchAllUsersRemotely() {
        userRemoteDataSource.fetchAllUsers()
    }

    override fun fetchUserLocally() {
        userDataSource.fetchUser("")
    }

    override fun fetchUserRemotely() {
        userRemoteDataSource.fetchUser("")
    }

    override fun editUserLocally() {
        userDataSource.editUser()
    }

    override fun editUserRemotely() {
        userRemoteDataSource.editUser()
    }

    override fun deleteUserLocally() {
        userDataSource.deleteUser("")
    }

    override fun deleteUserRemotely() {
        userRemoteDataSource.deleteUser("")
    }
}